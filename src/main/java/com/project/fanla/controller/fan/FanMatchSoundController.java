package com.project.fanla.controller.fan;

import com.project.fanla.model.entity.Match;
import com.project.fanla.model.entity.Sound;
import com.project.fanla.model.enums.SoundStatus;
import com.project.fanla.repository.MatchRepository;
import com.project.fanla.service.MatchSoundStateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for fan access to match sound information
 * This API is public and does not require authentication
 */
@RestController
@RequestMapping("/api/fan")
public class FanMatchSoundController {

    @Autowired
    private MatchRepository matchRepository;
    
    @Autowired
    private MatchSoundStateManager matchSoundStateManager;

    /**
     * Get the current sound status for a match
     * This endpoint is public and does not require authentication
     *
     * @param matchId the match ID
     * @return the sound status
     */
    @GetMapping("/matches/{matchId}/sound-status")
    public ResponseEntity<?> getMatchSoundStatus(@PathVariable Long matchId) {
        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Match not found"));
        }

        Match match = matchOptional.get();
        
        // Create response with sound information
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
        
        // Add match information
        response.put("status", match.getStatus());
        response.put("homeScore", match.getHomeScore());
        response.put("awayScore", match.getAwayScore());
        
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
            
            Sound activeSound = match.getActiveSound();
            response.put("activeSound", Map.of(
                "id", activeSound.getId(),
                "title", activeSound.getTitle(),
                "soundUrl", activeSound.getSoundUrl(),
                "soundImageUrl", activeSound.getSoundImageUrl()
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
    }
}
