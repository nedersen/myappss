package com.project.fanla.controller.admin;

import com.project.fanla.model.entity.Lyrics;
import com.project.fanla.model.entity.Match;
import com.project.fanla.model.entity.MatchSoundDetail;
import com.project.fanla.model.entity.Sound;
import com.project.fanla.model.entity.Team;
import com.project.fanla.model.entity.User;
import com.project.fanla.model.enums.EventType;
import com.project.fanla.model.enums.SoundStatus;
import com.project.fanla.payload.request.MatchSoundDetailRequest;
import com.project.fanla.payload.response.MatchSoundDetailResponse;
import com.project.fanla.payload.response.MessageResponse;
import com.project.fanla.repository.LyricsRepository;
import com.project.fanla.repository.MatchRepository;
import com.project.fanla.repository.MatchSoundDetailRepository;
import com.project.fanla.repository.SoundRepository;
import com.project.fanla.repository.UserRepository;
import com.project.fanla.service.SoundTimerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for managing match sound details by admin users
 */
@RestController
@RequestMapping("/api/admin/match-sound-details")
@PreAuthorize("hasRole('ROLE_Admin')")
public class MatchSoundDetailController {

    @Autowired
    private MatchSoundDetailRepository matchSoundDetailRepository;

    @Autowired
    private LyricsRepository lyricsRepository;
    
    @Autowired
    private SoundTimerService soundTimerService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private SoundRepository soundRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all match sound details for the admin's team
     *
     * @return list of match sound detail responses
     */
    @GetMapping
    public ResponseEntity<?> getAllMatchSoundDetails() {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        Team userTeam = user.getTeam();

        // Get match sound details for the team
        List<MatchSoundDetail> matchSoundDetails = matchSoundDetailRepository.findByTeamIdOrderByCreatedAtDesc(userTeam.getId());
        List<MatchSoundDetailResponse> matchSoundDetailResponses = matchSoundDetails.stream()
                .map(MatchSoundDetailResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(matchSoundDetailResponses);
    }

    /**
     * Get a specific match sound detail by ID
     *
     * @param id the match sound detail ID
     * @return the match sound detail response
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMatchSoundDetailById(@PathVariable Long id) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if match sound detail exists
        Optional<MatchSoundDetail> matchSoundDetailOptional = matchSoundDetailRepository.findById(id);
        if (matchSoundDetailOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match sound detail not found"));
        }

        MatchSoundDetail matchSoundDetail = matchSoundDetailOptional.get();
        Team userTeam = user.getTeam();

        // Verify the match belongs to the admin's team
        if (!matchSoundDetail.getMatch().getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match sound detail"));
        }

        return ResponseEntity.ok(MatchSoundDetailResponse.fromEntity(matchSoundDetail));
    }

    /**
     * Get all match sound details for a specific match
     *
     * @param matchId the match ID
     * @return list of match sound detail responses
     */
    @GetMapping("/match/{matchId}")
    public ResponseEntity<?> getMatchSoundDetails(@PathVariable Long matchId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match not found"));
        }

        Match match = matchOptional.get();
        Team userTeam = user.getTeam();

        // Verify the match belongs to the admin's team
        if (!match.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match"));
        }

        // Get match sound details for the match
        List<MatchSoundDetail> matchSoundDetails = matchSoundDetailRepository.findByMatchIdOrderByCreatedAtDesc(matchId);
        List<MatchSoundDetailResponse> matchSoundDetailResponses = matchSoundDetails.stream()
                .map(MatchSoundDetailResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(matchSoundDetailResponses);
    }

    /**
     * Get the latest match sound detail for a specific match with real-time position
     *
     * @param matchId the match ID
     * @return the latest match sound detail response with real-time position
     */
    @GetMapping("/match/{matchId}/latest")
    public ResponseEntity<?> getLatestMatchSoundDetail(@PathVariable Long matchId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match not found"));
        }

        Match match = matchOptional.get();
        Team userTeam = user.getTeam();

        // Verify the match belongs to the admin's team
        if (!match.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match"));
        }

        // Get latest match sound detail for the match
        Optional<MatchSoundDetail> latestMatchSoundDetailOptional = matchSoundDetailRepository.findLatestByMatchId(matchId);
        if (latestMatchSoundDetailOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("No match sound details found for this match"));
        }

        MatchSoundDetail latestMatchSoundDetail = latestMatchSoundDetailOptional.get();
        
        // Get real-time position from RAM if sound is active
        if (latestMatchSoundDetail.getActiveSound() != null) {
            Long soundId = latestMatchSoundDetail.getActiveSound().getId();
            Long currentPosition = soundTimerService.getCurrentPosition(userTeam.getId(), soundId);
            SoundStatus currentStatus = soundTimerService.getCurrentStatus(userTeam.getId(), soundId);
            
            // Update position and status with real-time values
            latestMatchSoundDetail.setCurrentMillisecond(currentPosition);
            latestMatchSoundDetail.setSoundStatus(currentStatus);
        }
        
        return ResponseEntity.ok(MatchSoundDetailResponse.fromEntity(latestMatchSoundDetail));
    }

    /**
     * Create a new match sound detail
     *
     * @param matchSoundDetailRequest the match sound detail request
     * @return the created match sound detail response
     */
    @PostMapping
    public ResponseEntity<?> createMatchSoundDetail(@Valid @RequestBody MatchSoundDetailRequest matchSoundDetailRequest) {
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
        Optional<Match> matchOptional = matchRepository.findById(matchSoundDetailRequest.getMatchId());
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

        // Check if sound exists
        Optional<Sound> soundOptional = soundRepository.findById(matchSoundDetailRequest.getSoundId());
        if (soundOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Sound not found"));
        }

        Sound sound = soundOptional.get();

        // Verify the sound belongs to the admin's team
        if (!sound.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this sound"));
        }

        // Get current millisecond
        Long currentMillisecond = matchSoundDetailRequest.getCurrentMillisecond();
        if (currentMillisecond == null) {
            currentMillisecond = 0L;
        }

        // Update sound's current position
        sound.setCurrentMillisecond(currentMillisecond);
        sound.setStatus(matchSoundDetailRequest.getSoundStatus());
        soundRepository.save(sound);

        // Create and save the match sound detail
        MatchSoundDetail matchSoundDetail = new MatchSoundDetail();
        matchSoundDetail.setMatch(match);
        matchSoundDetail.setActiveSound(sound);
        matchSoundDetail.setCurrentMillisecond(currentMillisecond);
        matchSoundDetail.setSoundStatus(matchSoundDetailRequest.getSoundStatus());
        matchSoundDetail.setEventType(matchSoundDetailRequest.getEventType());

        matchSoundDetail = matchSoundDetailRepository.save(matchSoundDetail);

        return ResponseEntity.status(HttpStatus.CREATED).body(MatchSoundDetailResponse.fromEntity(matchSoundDetail));
    }

    /**
     * Get all available sounds for a match
     *
     * @param matchId the match ID
     * @return list of sounds
     */
    @GetMapping("/match/{matchId}/sounds")
    public ResponseEntity<?> getAvailableSounds(@PathVariable Long matchId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match not found"));
        }

        Match match = matchOptional.get();
        Team userTeam = user.getTeam();

        // Verify the match belongs to the admin's team
        if (!match.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match"));
        }

        // Get all sounds for the team
        List<Sound> sounds = soundRepository.findByTeam(userTeam);
        return ResponseEntity.ok(sounds);
    }
    
    /**
     * Update sound status for a match
     *
     * @param matchId the match ID
     * @param soundId the sound ID
     * @param status the new sound status
     * @return the updated match sound detail response
     */
    @PutMapping("/match/{matchId}/sound/{soundId}/status")
    public ResponseEntity<?> updateSoundStatus(
            @PathVariable Long matchId,
            @PathVariable Long soundId,
            @RequestParam SoundStatus status) {
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

        // Check if sound exists
        Optional<Sound> soundOptional = soundRepository.findById(soundId);
        if (soundOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Sound not found"));
        }

        Sound sound = soundOptional.get();

        // Verify the sound belongs to the admin's team
        if (!sound.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this sound"));
        }

        // Determine event type based on status
        com.project.fanla.model.enums.EventType eventType;
        if (status == SoundStatus.STARTED) {
            eventType = com.project.fanla.model.enums.EventType.PLAY;
        } else if (status == SoundStatus.PAUSED) {
            eventType = com.project.fanla.model.enums.EventType.PAUSE;
        } else if (status == SoundStatus.STOPPED) {
            eventType = com.project.fanla.model.enums.EventType.STOP;
        } else {
            eventType = com.project.fanla.model.enums.EventType.PLAY;
        }

        // Update sound status
        sound.setStatus(status);
        soundRepository.save(sound);

        // Create and save the match sound detail
        MatchSoundDetail matchSoundDetail = new MatchSoundDetail();
        matchSoundDetail.setMatch(match);
        matchSoundDetail.setActiveSound(sound);
        matchSoundDetail.setCurrentMillisecond(sound.getCurrentMillisecond());
        matchSoundDetail.setSoundStatus(status);
        matchSoundDetail.setEventType(eventType);

        matchSoundDetail = matchSoundDetailRepository.save(matchSoundDetail);

        return ResponseEntity.ok(MatchSoundDetailResponse.fromEntity(matchSoundDetail));
    }
    
    /**
     * Update sound position for a match
     *
     * @param matchId the match ID
     * @param soundId the sound ID
     * @param millisecond the new position in milliseconds
     * @return the updated match sound detail response
     */
    @PutMapping("/match/{matchId}/sound/{soundId}/position")
    public ResponseEntity<?> updateSoundPosition(
            @PathVariable Long matchId,
            @PathVariable Long soundId,
            @RequestParam Long millisecond) {
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

        // Check if sound exists
        Optional<Sound> soundOptional = soundRepository.findById(soundId);
        if (soundOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Sound not found"));
        }

        Sound sound = soundOptional.get();

        // Verify the sound belongs to the admin's team
        if (!sound.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this sound"));
        }

        // Update position in RAM
        soundTimerService.seekTimer(userTeam.getId(), sound.getId(), millisecond);
        
        // Update sound position in DB
        sound.setCurrentMillisecond(millisecond);
        soundRepository.save(sound);

        // Create and save the match sound detail
        MatchSoundDetail matchSoundDetail = new MatchSoundDetail();
        matchSoundDetail.setMatch(match);
        matchSoundDetail.setActiveSound(sound);
        matchSoundDetail.setCurrentMillisecond(millisecond);
        matchSoundDetail.setSoundStatus(sound.getStatus());
        matchSoundDetail.setEventType(com.project.fanla.model.enums.EventType.SEEK);

        matchSoundDetail = matchSoundDetailRepository.save(matchSoundDetail);

        return ResponseEntity.ok(MatchSoundDetailResponse.fromEntity(matchSoundDetail));
    }
    
    /**
     * Start playing a sound for a match
     *
     * @param matchId the match ID
     * @param soundId the sound ID
     * @return the updated match sound detail response
     */
    @PostMapping("/match/{matchId}/sound/{soundId}/start")
    public ResponseEntity<?> startSound(
            @PathVariable Long matchId,
            @PathVariable Long soundId) {
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

        // Check if sound exists
        Optional<Sound> soundOptional = soundRepository.findById(soundId);
        if (soundOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Sound not found"));
        }

        Sound sound = soundOptional.get();

        // Verify the sound belongs to the admin's team
        if (!sound.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this sound"));
        }

        // Update sound status and position in RAM
        soundTimerService.startTimer(userTeam.getId(), sound.getId(), 0L);
        
        // Update sound status in DB
        sound.setStatus(SoundStatus.STARTED);
        sound.setCurrentMillisecond(0L);
        soundRepository.save(sound);

        // Create and save the match sound detail
        MatchSoundDetail matchSoundDetail = new MatchSoundDetail();
        matchSoundDetail.setMatch(match);
        matchSoundDetail.setActiveSound(sound);
        matchSoundDetail.setCurrentMillisecond(0L);
        matchSoundDetail.setSoundStatus(SoundStatus.STARTED);
        matchSoundDetail.setEventType(com.project.fanla.model.enums.EventType.START);

        matchSoundDetail = matchSoundDetailRepository.save(matchSoundDetail);

        return ResponseEntity.ok(MatchSoundDetailResponse.fromEntity(matchSoundDetail));
    }
    
    /**
     * Stop playing a sound for a match
     *
     * @param matchId the match ID
     * @param soundId the sound ID
     * @return the updated match sound detail response
     */
    @PostMapping("/match/{matchId}/sound/{soundId}/stop")
    public ResponseEntity<?> stopSound(
            @PathVariable Long matchId,
            @PathVariable Long soundId) {
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

        // Check if sound exists
        Optional<Sound> soundOptional = soundRepository.findById(soundId);
        if (soundOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Sound not found"));
        }

        Sound sound = soundOptional.get();

        // Verify the sound belongs to the admin's team
        if (!sound.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this sound"));
        }

        // Get current position from RAM
        Long currentPosition = soundTimerService.getCurrentPosition(userTeam.getId(), sound.getId());
        
        // Stop timer in RAM
        soundTimerService.stopTimer(userTeam.getId(), sound.getId());
        
        // Update sound status in DB
        sound.setStatus(SoundStatus.STOPPED);
        sound.setCurrentMillisecond(currentPosition);
        soundRepository.save(sound);

        // Create and save the match sound detail
        MatchSoundDetail matchSoundDetail = new MatchSoundDetail();
        matchSoundDetail.setMatch(match);
        matchSoundDetail.setActiveSound(sound);
        matchSoundDetail.setCurrentMillisecond(sound.getCurrentMillisecond());
        matchSoundDetail.setSoundStatus(SoundStatus.STOPPED);
        matchSoundDetail.setEventType(com.project.fanla.model.enums.EventType.STOP);

        matchSoundDetail = matchSoundDetailRepository.save(matchSoundDetail);

        return ResponseEntity.ok(MatchSoundDetailResponse.fromEntity(matchSoundDetail));
    }
    
    /**
     * Resume playing a sound for a match
     *
     * @param matchId the match ID
     * @param soundId the sound ID
     * @return the updated match sound detail response
     */
    @PostMapping("/match/{matchId}/sound/{soundId}/resume")
    public ResponseEntity<?> resumeSound(
            @PathVariable Long matchId,
            @PathVariable Long soundId) {
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

        // Check if sound exists
        Optional<Sound> soundOptional = soundRepository.findById(soundId);
        if (soundOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Sound not found"));
        }

        Sound sound = soundOptional.get();

        // Verify the sound belongs to the admin's team
        if (!sound.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this sound"));
        }

        // Resume timer in RAM
        soundTimerService.resumeTimer(userTeam.getId(), sound.getId());
        
        // Update sound status in DB
        sound.setStatus(SoundStatus.STARTED);
        soundRepository.save(sound);

        // Create and save the match sound detail
        MatchSoundDetail matchSoundDetail = new MatchSoundDetail();
        matchSoundDetail.setMatch(match);
        matchSoundDetail.setActiveSound(sound);
        matchSoundDetail.setCurrentMillisecond(sound.getCurrentMillisecond());
        matchSoundDetail.setSoundStatus(SoundStatus.STARTED);
        matchSoundDetail.setEventType(com.project.fanla.model.enums.EventType.RESUME);

        matchSoundDetail = matchSoundDetailRepository.save(matchSoundDetail);

        return ResponseEntity.ok(MatchSoundDetailResponse.fromEntity(matchSoundDetail));
    }
    
    /**
     * Pause playing a sound for a match
     *
     * @param matchId the match ID
     * @param soundId the sound ID
     * @return the updated match sound detail response
     */
    @PostMapping("/match/{matchId}/sound/{soundId}/pause")
    public ResponseEntity<?> pauseSound(
            @PathVariable Long matchId,
            @PathVariable Long soundId) {
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

        // Check if sound exists
        Optional<Sound> soundOptional = soundRepository.findById(soundId);
        if (soundOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Sound not found"));
        }

        Sound sound = soundOptional.get();

        // Verify the sound belongs to the admin's team
        if (!sound.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this sound"));
        }

        // Get current position from RAM
        Long currentPosition = soundTimerService.getCurrentPosition(userTeam.getId(), sound.getId());
        
        // Pause timer in RAM
        soundTimerService.pauseTimer(userTeam.getId(), sound.getId());
        
        // Update sound status in DB
        sound.setStatus(SoundStatus.PAUSED);
        sound.setCurrentMillisecond(currentPosition);
        soundRepository.save(sound);

        // Create and save the match sound detail
        MatchSoundDetail matchSoundDetail = new MatchSoundDetail();
        matchSoundDetail.setMatch(match);
        matchSoundDetail.setActiveSound(sound);
        matchSoundDetail.setCurrentMillisecond(sound.getCurrentMillisecond());
        matchSoundDetail.setSoundStatus(SoundStatus.PAUSED);
        matchSoundDetail.setEventType(com.project.fanla.model.enums.EventType.PAUSE);

        matchSoundDetail = matchSoundDetailRepository.save(matchSoundDetail);

        return ResponseEntity.ok(MatchSoundDetailResponse.fromEntity(matchSoundDetail));
    }
    
    /**
     * Get real-time position and status for a sound
     *
     * @param matchId the match ID
     * @param soundId the sound ID
     * @return the real-time position and status
     */
    @GetMapping("/match/{matchId}/sound/{soundId}/real-time")
    public ResponseEntity<?> getRealTimePosition(
            @PathVariable Long matchId,
            @PathVariable Long soundId) {
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

        // Check if sound exists
        Optional<Sound> soundOptional = soundRepository.findById(soundId);
        if (soundOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Sound not found"));
        }

        Sound sound = soundOptional.get();

        // Verify the sound belongs to the admin's team
        if (!sound.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this sound"));
        }

        // Get real-time position and status from RAM
        Long currentPosition = soundTimerService.getCurrentPosition(userTeam.getId(), soundId);
        SoundStatus currentStatus = soundTimerService.getCurrentStatus(userTeam.getId(), soundId);
        
        // Find current lyric based on the current second
        int currentSecond = (int) (currentPosition / 1000);
        String currentLyricText = "";
        
        // Find the closest lyric to the current second
        List<Lyrics> lyrics = lyricsRepository.findBySoundIdOrderBySecondAsc(sound.getId());
        for (Lyrics lyric : lyrics) {
            if (lyric.getSecond() <= currentSecond) {
                currentLyricText = lyric.getLyric();
            } else {
                break;
            }
        }
        
        // Create a response with real-time data
        Map<String, Object> response = new HashMap<>();
        response.put("soundId", soundId);
        response.put("soundTitle", sound.getTitle());
        response.put("soundUrl", sound.getSoundUrl());
        response.put("soundImageUrl", sound.getSoundImageUrl());
        response.put("currentMillisecond", currentPosition);
        response.put("currentSecond", currentSecond);
        response.put("currentLyric", currentLyricText);
        response.put("status", currentStatus);
        
        return ResponseEntity.ok(response);
    }
}
