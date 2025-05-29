package com.project.fanla.service;

import com.project.fanla.event.SoundEvent;
import com.project.fanla.model.enums.SoundStatus;
import com.project.fanla.repository.MatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SoundTimerService {
    
    private static final Logger logger = LoggerFactory.getLogger(SoundTimerService.class);
    
    // Key: teamId_soundId, Value: SoundTimer
    private final Map<String, SoundTimer> soundTimers = new ConcurrentHashMap<>();
    
    // Scheduled executor for updating timers
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @Autowired
    private MatchSoundStateManager matchSoundStateManager;
    
    @Autowired
    private MatchRepository matchRepository;
    
    public SoundTimerService() {
        // Start a scheduler to update all active timers every 100ms
        scheduler.scheduleAtFixedRate(this::updateAllTimers, 0, 100, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Start a sound timer for a team's sound
     * 
     * @param teamId the team ID
     * @param soundId the sound ID
     * @param initialPosition the initial position in milliseconds
     */
    public void startTimer(Long teamId, Long soundId, Long initialPosition) {
        String key = getKey(teamId, soundId);
        SoundTimer timer = new SoundTimer(initialPosition, SoundStatus.STARTED);
        soundTimers.put(key, timer);
    }
    
    /**
     * Stop a sound timer for a team's sound
     * 
     * @param teamId the team ID
     * @param soundId the sound ID
     */
    public void stopTimer(Long teamId, Long soundId) {
        String key = getKey(teamId, soundId);
        SoundTimer timer = soundTimers.get(key);
        if (timer != null) {
            timer.setStatus(SoundStatus.STOPPED);
        }
    }
    
    /**
     * Pause a sound timer for a team's sound
     * 
     * @param teamId the team ID
     * @param soundId the sound ID
     */
    public void pauseTimer(Long teamId, Long soundId) {
        String key = getKey(teamId, soundId);
        SoundTimer timer = soundTimers.get(key);
        if (timer != null) {
            timer.setStatus(SoundStatus.PAUSED);
        }
    }
    
    /**
     * Resume a sound timer for a team's sound
     * 
     * @param teamId the team ID
     * @param soundId the sound ID
     */
    public void resumeTimer(Long teamId, Long soundId) {
        String key = getKey(teamId, soundId);
        SoundTimer timer = soundTimers.get(key);
        if (timer != null) {
            timer.setStatus(SoundStatus.STARTED);
            timer.setLastUpdateTime(System.currentTimeMillis());
        }
    }
    
    /**
     * Seek a sound timer for a team's sound
     * 
     * @param teamId the team ID
     * @param soundId the sound ID
     * @param position the position in milliseconds
     */
    public void seekTimer(Long teamId, Long soundId, Long position) {
        String key = getKey(teamId, soundId);
        SoundTimer timer = soundTimers.get(key);
        if (timer != null) {
            timer.setPosition(position);
            timer.setLastUpdateTime(System.currentTimeMillis());
        } else {
            // If timer doesn't exist, create a new one in paused state
            timer = new SoundTimer(position, SoundStatus.PAUSED);
            soundTimers.put(key, timer);
        }
    }
    
    /**
     * Get the current position of a team's sound
     * 
     * @param teamId the team ID
     * @param soundId the sound ID
     * @return the current position in milliseconds, or 0 if no timer exists
     */
    public Long getCurrentPosition(Long teamId, Long soundId) {
        String key = getKey(teamId, soundId);
        SoundTimer timer = soundTimers.get(key);
        return timer != null ? timer.getPosition() : 0L;
    }
    
    /**
     * Get the current status of a team's sound
     * 
     * @param teamId the team ID
     * @param soundId the sound ID
     * @return the current status, or STOPPED if no timer exists
     */
    public SoundStatus getCurrentStatus(Long teamId, Long soundId) {
        String key = getKey(teamId, soundId);
        SoundTimer timer = soundTimers.get(key);
        return timer != null ? timer.getStatus() : SoundStatus.STOPPED;
    }
    
    // Takım ID'si ve ses ID'si ile eşleşen maç ID'lerini tutan önbellek
    private final Map<String, Long> soundToMatchCache = new ConcurrentHashMap<>();
    
    /**
     * Ses ve maç arasındaki ilişkiyi önbelleğe ekler
     * 
     * @param matchId maç ID
     * @param teamId takım ID
     * @param soundId ses ID
     */
    public void registerSoundMatch(Long matchId, Long teamId, Long soundId) {
        String key = getKey(teamId, soundId);
        soundToMatchCache.put(key, matchId);
        logger.debug("Registered sound {} for team {} with match {}", soundId, teamId, matchId);
    }
    
    /**
     * Update all active timers
     */
    private void updateAllTimers() {
        long currentTime = System.currentTimeMillis();
        soundTimers.forEach((key, timer) -> {
            if (timer.getStatus() == SoundStatus.STARTED) {
                long elapsed = currentTime - timer.getLastUpdateTime();
                timer.setPosition(timer.getPosition() + elapsed);
                timer.setLastUpdateTime(currentTime);
                
                // Önbellekten maç ID'sini al
                Long matchId = soundToMatchCache.get(key);
                if (matchId != null) {
                    // MatchSoundStateManager'ı güncelle (RAM'de)
                    matchSoundStateManager.updatePosition(matchId, timer.getPosition());
                    logger.debug("Updated position for match {} to {}", matchId, timer.getPosition());
                }
            }
        });
    }
    
    /**
     * Get the key for a team's sound
     * 
     * @param teamId the team ID
     * @param soundId the sound ID
     * @return the key
     */
    private String getKey(Long teamId, Long soundId) {
        return teamId + "_" + soundId;
    }
    
    /**
     * Ses olaylarını dinler ve önbelleği günceller
     * 
     * @param event Ses olayı
     */
    @EventListener
    public void handleSoundEvent(SoundEvent event) {
        switch (event.getEventType()) {
            case CREATED:
                logger.info("New sound created: {}", event.getSound().getId());
                // Yeni ses eklendiğinde bir şey yapmaya gerek yok, 
                // kullanıldığında registerSoundMatch ile kaydedilecek
                break;
                
            case UPDATED:
                logger.info("Sound updated: {}", event.getSound().getId());
                // Eğer bu ses için bir timer varsa, durumunu güncelle
                soundTimers.keySet().stream()
                    .filter(key -> key.endsWith("_" + event.getSound().getId()))
                    .forEach(key -> {
                        // Timer'a dokunma, sadece log
                        logger.debug("Found timer for updated sound: {}", key);
                    });
                break;
                
            case DELETED:
                logger.info("Sound deleted: {}", event.getSound().getId());
                // Eğer bu ses için bir timer varsa, sil
                soundTimers.keySet().stream()
                    .filter(key -> key.endsWith("_" + event.getSound().getId()))
                    .forEach(key -> {
                        soundTimers.remove(key);
                        logger.debug("Removed timer for deleted sound: {}", key);
                    });
                
                // Eğer bu ses için bir maç ilişkisi varsa, sil
                soundToMatchCache.keySet().stream()
                    .filter(key -> key.endsWith("_" + event.getSound().getId()))
                    .forEach(key -> {
                        soundToMatchCache.remove(key);
                        logger.debug("Removed match relation for deleted sound: {}", key);
                    });
                break;
        }
    }
    
    /**
     * Inner class to represent a sound timer
     */
    private static class SoundTimer {
        private Long position;
        private SoundStatus status;
        private long lastUpdateTime;
        
        public SoundTimer(Long position, SoundStatus status) {
            this.position = position;
            this.status = status;
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        public Long getPosition() {
            return position;
        }
        
        public void setPosition(Long position) {
            this.position = position;
        }
        
        public SoundStatus getStatus() {
            return status;
        }
        
        public void setStatus(SoundStatus status) {
            this.status = status;
        }
        
        public long getLastUpdateTime() {
            return lastUpdateTime;
        }
        
        public void setLastUpdateTime(long lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }
    }
}
