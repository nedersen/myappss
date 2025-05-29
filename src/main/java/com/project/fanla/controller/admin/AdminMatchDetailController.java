package com.project.fanla.controller.admin;

import com.project.fanla.model.entity.Match;
import com.project.fanla.model.entity.Sound;
import com.project.fanla.model.entity.Team;
import com.project.fanla.model.entity.User;
import com.project.fanla.model.enums.SoundStatus;
import com.project.fanla.payload.response.MessageResponse;
import com.project.fanla.repository.MatchRepository;
import com.project.fanla.repository.SoundRepository;
import com.project.fanla.repository.UserRepository;
import com.project.fanla.service.MatchSoundStateManager;
import com.project.fanla.service.SoundTimerService;
import com.project.fanla.handler.MatchWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/admin/match-details")
public class AdminMatchDetailController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminMatchDetailController.class);
    
    // Kullanıcı takım önbelleği
    private final Map<String, Team> userTeamCache = new ConcurrentHashMap<>();
    
    // Maç önbelleği
    private final Map<Long, Match> matchCache = new ConcurrentHashMap<>();
    
    // Takım sesleri önbelleği
    private final Map<Long, List<Sound>> teamSoundsCache = new ConcurrentHashMap<>();
    
    // Ses önbelleği
    private final Map<Long, Sound> soundCache = new ConcurrentHashMap<>();

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private SoundRepository soundRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SoundTimerService soundTimerService;
    
    @Autowired
    private MatchSoundStateManager matchSoundStateManager;
    
    @Autowired
    private MatchWebSocketHandler matchWebSocketHandler;
    
    /**
     * Uygulama başlatıldığında tüm verileri önbelleğe yükler
     */
    @PostConstruct
    public void init() {
        logger.info("Initializing AdminMatchDetailController cache...");
        
        // Tüm maçları önbelleğe al
        matchRepository.findAll().forEach(match -> {
            matchCache.put(match.getId(), match);
        });
        
        // Tüm sesleri önbelleğe al
        soundRepository.findAll().forEach(sound -> {
            soundCache.put(sound.getId(), sound);
            
            // Takım seslerini grupla
            Long teamId = sound.getTeam().getId();
            teamSoundsCache.computeIfAbsent(teamId, k -> new java.util.ArrayList<>()).add(sound);
        });
        
        logger.info("Cache initialized with {} matches and {} sounds", matchCache.size(), soundCache.size());
    }
    
    /**
     * Kullanıcının takımını önbellekten veya veritabanından alır
     * 
     * @return Kullanıcının takımı
     */
    private Team getUserTeam() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Önbellekte var mı kontrol et
        if (userTeamCache.containsKey(username)) {
            return userTeamCache.get(username);
        }
        
        // Veritabanından al ve önbelleğe ekle
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && user.getTeam() != null) {
            userTeamCache.put(username, user.getTeam());
            return user.getTeam();
        }
        
        return null;
    }
    
    /**
     * Maça erişim izni olup olmadığını kontrol eder
     * 
     * @param matchId Maç ID
     * @return Erişim izni varsa true, yoksa false
     */
    private boolean checkMatchAccess(Long matchId) {
        // Kullanıcının takımını al
        Team userTeam = getUserTeam();
        if (userTeam == null) {
            return false;
        }
        
        // Maçı önbellekten al
        Match match = matchCache.get(matchId);
        if (match == null) {
            // Önbellekte yoksa veritabanından kontrol et
            Optional<Match> matchOptional = matchRepository.findById(matchId);
            if (matchOptional.isEmpty()) {
                return false;
            }
            
            // Maçı önbelleğe ekle
            match = matchOptional.get();
            matchCache.put(matchId, match);
        }
        
        // Maçın kullanıcının takımına ait olup olmadığını kontrol et
        return match.getTeam().getId().equals(userTeam.getId());
    }

    /**
     * Get match details including sound information
     *
     * @param matchId the match ID
     * @return the match details
     */
    @GetMapping("/match/{matchId}")
    public ResponseEntity<?> getMatchDetails(@PathVariable Long matchId) {
        try {
            // Kullanıcının maça erişimi olup olmadığını kontrol et
            if (!checkMatchAccess(matchId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("You don't have access to this match"));
            }

            // Önbellekten maçı al
            Match match = matchCache.get(matchId);
            if (match == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Match not found"));
            }
            
            // Create response with match details
            Map<String, Object> response = new HashMap<>();
            response.put("matchId", match.getId());
            
            // Add team information
            response.put("teamId", match.getTeam().getId());
            response.put("teamName", match.getTeam().getName());
            response.put("teamLogo", match.getTeam().getLogoUrl());
            
            // Add opponent information
            if (match.getOpponentTeam() != null) {
                response.put("opponentTeamId", match.getOpponentTeam().getId());
                response.put("opponentTeamName", match.getOpponentTeam().getName());
                response.put("opponentTeamLogo", match.getOpponentTeam().getLogoUrl());
                response.put("manualOpponentName", null);
                response.put("manualOpponentLogo", null);
            } else {
                response.put("opponentTeamId", null);
                response.put("opponentTeamName", null);
                response.put("opponentTeamLogo", null);
                response.put("manualOpponentName", match.getManualOpponentName());
                response.put("manualOpponentLogo", match.getManualOpponentLogo());
            }
            
            response.put("status", match.getStatus());
            response.put("homeScore", match.getHomeScore());
            response.put("awayScore", match.getAwayScore());
            response.put("matchDate", match.getMatchDate());
            
            // Get sound information from RAM
            MatchSoundStateManager.MatchSoundState state = matchSoundStateManager.getState(matchId);
            
            // If state exists in RAM, use it
            if (state != null) {
                if (state.getActiveSoundId() != null) {
                    response.put("activeSound", Map.of(
                        "id", state.getActiveSoundId(),
                        "title", state.getActiveSoundTitle(),
                        "soundUrl", state.getActiveSoundUrl(),
                        "soundImageUrl", state.getActiveSoundImageUrl()
                    ));
                } else {
                    response.put("activeSound", null);
                }
                response.put("soundStatus", state.getSoundStatus());
                response.put("currentMillisecond", state.getCurrentMillisecond());
                response.put("soundUpdatedAt", state.getUpdatedAt());
            } 
            // Otherwise, use the database values and initialize RAM state
            else if (match.getActiveSound() != null) {
                matchSoundStateManager.updateState(matchId, match);
                
                response.put("activeSound", Map.of(
                    "id", match.getActiveSound().getId(),
                    "title", match.getActiveSound().getTitle(),
                    "soundUrl", match.getActiveSound().getSoundUrl(),
                    "soundImageUrl", match.getActiveSound().getSoundImageUrl()
                ));
                response.put("soundStatus", match.getSoundStatus());
                response.put("currentMillisecond", match.getCurrentMillisecond());
                response.put("soundUpdatedAt", match.getSoundUpdatedAt());
            } else {
                response.put("activeSound", null);
                response.put("soundStatus", SoundStatus.STOPPED);
                response.put("currentMillisecond", 0);
                response.put("soundUpdatedAt", null);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting match details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error getting match details: " + e.getMessage()));
        }
    }

    /**
     * Start playing a sound for a match
     *
     * @param matchId the match ID
     * @param soundId the sound ID
     * @return the updated match details
     */
    @PostMapping("/match/{matchId}/sound/{soundId}/start")
    public ResponseEntity<?> startSound(@PathVariable Long matchId, @PathVariable Long soundId) {
        try {
            // Kullanıcının maça erişimi olup olmadığını kontrol et
            if (!checkMatchAccess(matchId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("You don't have access to this match"));
            }

            // Önbellekten maçı al
            Match match = matchCache.get(matchId);
            if (match == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Match not found"));
            }

            // Önbellekten sesi al
            Sound sound = soundCache.get(soundId);
            if (sound == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Sound not found"));
            }

            // Sesin kullanıcının takımına ait olup olmadığını kontrol et
            Team userTeam = getUserTeam();
            if (!sound.getTeam().getId().equals(userTeam.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("You don't have access to this sound"));
            }
            
            // RAM'de sesin zaten çalıp çalmadığını kontrol et
            MatchSoundStateManager.MatchSoundState currentState = matchSoundStateManager.getState(matchId);
            
            // Ses zaten çalıyorsa, bir şey yapma
            if (currentState != null && 
                currentState.getActiveSoundId() != null && 
                currentState.getActiveSoundId().equals(soundId) &&
                currentState.getSoundStatus() == SoundStatus.STARTED) {
                
                return ResponseEntity.ok(new MessageResponse("Sound is already playing"));
            }
            
            // Maçı yeni ses ile güncelle (sadece RAM'de)
            match.setActiveSound(sound);
            match.setSoundStatus(SoundStatus.STARTED);
            match.setCurrentMillisecond(0L);
            match.setSoundUpdatedAt(new Date());
            
            // Önbellekteki maçı güncelle
            matchCache.put(matchId, match);
            
            // RAM durumunu güncelle
            matchSoundStateManager.startSound(matchId, soundId, sound.getTitle(), sound.getSoundUrl(), sound.getSoundImageUrl());
            
            // Ses zamanlayıcısını başlat
            soundTimerService.startTimer(match.getTeam().getId(), sound.getId(), 0L);
            
            // Ses ve maç arasındaki ilişkiyi önbelleğe kaydet
            soundTimerService.registerSoundMatch(matchId, match.getTeam().getId(), sound.getId());
            
            // WebSocket aracılığıyla ses güncellemesini yayınla
            matchWebSocketHandler.broadcastSoundUpdate(matchId);
            
            // Veritabanını asenkron olarak güncelle (performansı etkilememesi için)
            CompletableFuture.runAsync(() -> {
                try {
                    // Önce en güncel maçı veritabanından al
                    matchRepository.findById(matchId).ifPresent(dbMatch -> {
                        // Sadece ses bilgilerini güncelle
                        dbMatch.setActiveSound(sound);
                        dbMatch.setSoundStatus(SoundStatus.STARTED);
                        dbMatch.setCurrentMillisecond(0L);
                        dbMatch.setSoundUpdatedAt(new Date());
                        
                        // Veritabanında maçı güncelle
                        matchRepository.save(dbMatch);
                        logger.debug("Match {} updated in database asynchronously", matchId);
                    });
                } catch (Exception e) {
                    logger.error("Error updating match in database: {}", e.getMessage());
                }
            });
            
            // Yanıt oluştur
            Map<String, Object> response = new HashMap<>();
            response.put("matchId", match.getId());
            response.put("activeSound", Map.of(
                "id", sound.getId(),
                "title", sound.getTitle(),
                "soundUrl", sound.getSoundUrl(),
                "soundImageUrl", sound.getSoundImageUrl()
            ));
            response.put("soundStatus", SoundStatus.STARTED);
            response.put("currentMillisecond", 0L);
            response.put("soundUpdatedAt", match.getSoundUpdatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error starting sound", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error starting sound: " + e.getMessage()));
        }
    }

    /**
     * Stop playing a sound for a match
     *
     * @param matchId the match ID
     * @return the updated match details
     */
    @PostMapping("/match/{matchId}/sound/stop")
    public ResponseEntity<?> stopSound(@PathVariable Long matchId) {
        try {
            // Kullanıcının maça erişimi olup olmadığını kontrol et
            if (!checkMatchAccess(matchId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("You don't have access to this match"));
            }

            // Önbellekten maçı al
            Match match = matchCache.get(matchId);
            if (match == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Match not found"));
            }

            // RAM'deki ses durumunu kontrol et
            MatchSoundStateManager.MatchSoundState currentState = matchSoundStateManager.getState(matchId);
            
            // Aktif ses yoksa veya zaten durdurulmuşsa, hata dön
            if (currentState == null || currentState.getActiveSoundId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("No active sound to stop"));
            }
            
            if (currentState.getSoundStatus() == SoundStatus.STOPPED) {
                return ResponseEntity.ok(new MessageResponse("Sound is already stopped"));
            }

            // Ses zamanlayıcısını durdur
            if (match.getActiveSound() != null) {
                soundTimerService.stopTimer(match.getTeam().getId(), match.getActiveSound().getId());
            }
            
            // RAM durumunu güncelle
            matchSoundStateManager.stopSound(matchId);
            
            // Maçı güncelle (sadece RAM'de)
            match.setSoundStatus(SoundStatus.STOPPED);
            match.setSoundUpdatedAt(new Date());
            
            // Önbellekteki maçı güncelle
            matchCache.put(matchId, match);
            
            // WebSocket aracılığıyla ses güncellemesini yayınla
            matchWebSocketHandler.broadcastSoundUpdate(matchId);
            
            // Yanıt oluştur
            Map<String, Object> response = new HashMap<>();
            response.put("matchId", match.getId());
            if (match.getActiveSound() != null) {
                response.put("activeSound", Map.of(
                    "id", match.getActiveSound().getId(),
                    "title", match.getActiveSound().getTitle(),
                    "soundUrl", match.getActiveSound().getSoundUrl(),
                    "soundImageUrl", match.getActiveSound().getSoundImageUrl()
                ));
            } else {
                response.put("activeSound", null);
            }
            response.put("soundStatus", match.getSoundStatus());
            response.put("currentMillisecond", match.getCurrentMillisecond());
            response.put("soundUpdatedAt", match.getSoundUpdatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error stopping sound", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error stopping sound: " + e.getMessage()));
        }
    }

    /**
     * Pause playing a sound for a match
     *
     * @param matchId the match ID
     * @return the updated match details
     */
    @PostMapping("/match/{matchId}/sound/pause")
    public ResponseEntity<?> pauseSound(@PathVariable Long matchId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        Team userTeam = user.getTeam();

        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match not found"));
        }

        Match match = matchOptional.get();

        // Verify the match belongs to the admin's team
        if (!match.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match"));
        }

        // Check if there is an active sound
        if (match.getActiveSound() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("No active sound to pause"));
        }

        Sound sound = match.getActiveSound();

        // Pause the sound timer
        soundTimerService.pauseTimer(userTeam.getId(), sound.getId());
        
        // Get current position
        Long currentPosition = soundTimerService.getCurrentPosition(userTeam.getId(), sound.getId());
        
        // Update sound status in RAM
        matchSoundStateManager.pauseSound(matchId, currentPosition);
        
        // Update match in database
        match.setSoundStatus(SoundStatus.PAUSED);
        match.setCurrentMillisecond(currentPosition);
        match.setSoundUpdatedAt(new Date());
        matchRepository.save(match);
        
        // WebSocket üzerinden ses durumunu yayınla
        logger.info("Broadcasting sound update for match {}", matchId);
        matchWebSocketHandler.broadcastSoundUpdate(matchId);

        // Create response with updated match details
        Map<String, Object> response = new HashMap<>();
        response.put("matchId", match.getId());
        response.put("activeSound", Map.of(
            "id", match.getActiveSound().getId(),
            "title", match.getActiveSound().getTitle(),
            "soundUrl", match.getActiveSound().getSoundUrl(),
            "soundImageUrl", match.getActiveSound().getSoundImageUrl()
        ));
        response.put("soundStatus", match.getSoundStatus());
        response.put("currentMillisecond", match.getCurrentMillisecond());
        response.put("soundUpdatedAt", match.getSoundUpdatedAt());

        return ResponseEntity.ok(response);
    }

    /**
     * Resume playing a sound for a match
     *
     * @param matchId the match ID
     * @return the updated match details
     */
    @PostMapping("/match/{matchId}/sound/resume")
    public ResponseEntity<?> resumeSound(@PathVariable Long matchId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        Team userTeam = user.getTeam();

        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match not found"));
        }

        Match match = matchOptional.get();

        // Verify the match belongs to the admin's team
        if (!match.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match"));
        }

        // Check if there is an active sound
        if (match.getActiveSound() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("No active sound to resume"));
        }

        Sound sound = match.getActiveSound();

        // Resume the sound timer from current position
        soundTimerService.resumeTimer(userTeam.getId(), sound.getId());
        
        // Update sound status in RAM
        matchSoundStateManager.resumeSound(matchId);
        
        // Update match in database
        match.setSoundStatus(SoundStatus.STARTED);
        match.setSoundUpdatedAt(new Date());
        matchRepository.save(match);
        
        // WebSocket üzerinden ses durumunu yayınla
        logger.info("Broadcasting sound update for match {}", matchId);
        matchWebSocketHandler.broadcastSoundUpdate(matchId);

        // Create response with updated match details
        Map<String, Object> response = new HashMap<>();
        response.put("matchId", match.getId());
        response.put("activeSound", Map.of(
            "id", match.getActiveSound().getId(),
            "title", match.getActiveSound().getTitle(),
            "soundUrl", match.getActiveSound().getSoundUrl(),
            "soundImageUrl", match.getActiveSound().getSoundImageUrl()
        ));
        response.put("soundStatus", match.getSoundStatus());
        response.put("currentMillisecond", match.getCurrentMillisecond());
        response.put("soundUpdatedAt", match.getSoundUpdatedAt());

        return ResponseEntity.ok(response);
    }

    /**
     * Seek to a specific position in the sound
     *
     * @param matchId the match ID
     * @param position the position in milliseconds
     * @return the updated match details
     */
    @PostMapping("/match/{matchId}/sound/seek")
    public ResponseEntity<?> seekSound(
            @PathVariable Long matchId,
            @RequestParam Long position) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        Team userTeam = user.getTeam();

        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match not found"));
        }

        Match match = matchOptional.get();

        // Verify the match belongs to the admin's team
        if (!match.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match"));
        }

        // Check if there is an active sound
        if (match.getActiveSound() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("No active sound to seek"));
        }

        Sound sound = match.getActiveSound();

        // Seek to the specified position
        soundTimerService.seekTimer(userTeam.getId(), sound.getId(), position);
        
        // Update sound position in RAM
        matchSoundStateManager.seekSound(matchId, position);
        
        // WebSocket üzerinden ses durumunu yayınla
        logger.info("Broadcasting sound update for match {}", matchId);
        matchWebSocketHandler.broadcastSoundUpdate(matchId);
        
        // We don't need to update the database for seek operations
        // This improves performance for frequent seek operations

        // Create response with updated match details
        Map<String, Object> response = new HashMap<>();
        response.put("matchId", match.getId());
        response.put("activeSound", Map.of(
            "id", match.getActiveSound().getId(),
            "title", match.getActiveSound().getTitle(),
            "soundUrl", match.getActiveSound().getSoundUrl(),
            "soundImageUrl", match.getActiveSound().getSoundImageUrl()
        ));
        response.put("soundStatus", match.getSoundStatus());
        response.put("currentMillisecond", match.getCurrentMillisecond());
        response.put("soundUpdatedAt", match.getSoundUpdatedAt());

        return ResponseEntity.ok(response);
    }

    /**
     * Get all sounds for a match's team
     *
     * @param matchId the match ID
     * @return the list of sounds
     */
    @GetMapping("/match/{matchId}/sounds")
    public ResponseEntity<?> getMatchSounds(@PathVariable Long matchId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        Team userTeam = user.getTeam();

        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match not found"));
        }

        Match match = matchOptional.get();

        // Verify the match belongs to the admin's team
        if (!match.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match"));
        }

        // Get all sounds for the team
        return ResponseEntity.ok(soundRepository.findByTeam(userTeam));
    }
}
