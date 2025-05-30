package com.project.fanla.service;

import com.project.fanla.event.SoundEvent;
import com.project.fanla.model.entity.Sound;
import com.project.fanla.model.entity.Team;
import com.project.fanla.repository.SoundRepository;
import com.project.fanla.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ses dosyalarının önbelleğe alınmasını sağlayan servis
 * Bu servis, ses dosyalarının URL'lerini ve diğer bilgilerini hafızada tutar
 * Böylece her istek için veritabanına gitmek gerekmez
 */
@Service
public class SoundCacheService {
    
    private static final Logger logger = LoggerFactory.getLogger(SoundCacheService.class);
    
    // TeamId -> List<Sound> şeklinde önbellek
    private final Map<Long, List<Sound>> teamSoundsCache = new ConcurrentHashMap<>();
    
    @Autowired
    private SoundRepository soundRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    /**
     * Uygulama başlatıldığında tüm takımların seslerini önbelleğe al
     */
    @PostConstruct
    public void init() {
        logger.info("Initializing sound cache for all teams");
        List<Team> allTeams = teamRepository.findAll();
        
        for (Team team : allTeams) {
            refreshTeamSoundsCache(team.getId());
        }
        
        logger.info("Sound cache initialized for {} teams", allTeams.size());
    }
    
    /**
     * Bir takımın tüm seslerini önbelleğe al
     * 
     * @param teamId Takım ID
     * @return Takımın sesleri
     */
    public List<Sound> getTeamSounds(Long teamId) {
        // Önbellekte yoksa, veritabanından al ve önbelleğe ekle
        if (!teamSoundsCache.containsKey(teamId)) {
            refreshTeamSoundsCache(teamId);
        }
        
        return teamSoundsCache.get(teamId);
    }
    
    /**
     * Bir takımın ses önbelleğini güncelle
     * 
     * @param teamId Takım ID
     */
    public void refreshTeamSoundsCache(Long teamId) {
        logger.debug("Refreshing sound cache for team {}", teamId);
        
        // TeamId'ye göre Team nesnesini bul
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
        
        // Team nesnesine göre sesleri getir
        List<Sound> teamSounds = soundRepository.findByTeam(team);
        teamSoundsCache.put(teamId, teamSounds);
        logger.debug("Sound cache refreshed for team {}: {} sounds", teamId, teamSounds.size());
    }
    
    /**
     * Ses olaylarını dinleyerek önbelleği güncelle
     * 
     * @param event Ses olayı
     */
    @EventListener
    @Async
    public void handleSoundEvent(SoundEvent event) {
        Sound sound = event.getSound();
        Long teamId = sound.getTeam().getId();
        
        switch (event.getEventType()) {
            case CREATED:
                logger.info("Sound created event received: {}", sound.getId());
                refreshTeamSoundsCache(teamId);
                break;
                
            case UPDATED:
                logger.info("Sound updated event received: {}", sound.getId());
                refreshTeamSoundsCache(teamId);
                break;
                
            case DELETED:
                logger.info("Sound deleted event received: {}", sound.getId());
                refreshTeamSoundsCache(teamId);
                break;
        }
    }
    
    /**
     * Önbelleği tamamen temizle
     */
    public void clearCache() {
        logger.info("Clearing sound cache");
        teamSoundsCache.clear();
    }
}
