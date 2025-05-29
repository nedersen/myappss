package com.project.fanla.service;

import com.project.fanla.model.entity.Match;
import com.project.fanla.model.entity.Sound;
import com.project.fanla.model.enums.SoundStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing match sound state in memory
 * This avoids database operations for frequent updates
 */
@Service
public class MatchSoundStateManager {
    
    private static final Logger logger = LoggerFactory.getLogger(MatchSoundStateManager.class);
    
    // Key: matchId, Value: MatchSoundState
    private final Map<Long, MatchSoundState> matchSoundStates = new ConcurrentHashMap<>();
    
    /**
     * Get the sound state for a match
     * 
     * @param matchId the match ID
     * @return the match sound state, or null if not found
     */
    public MatchSoundState getState(Long matchId) {
        return matchSoundStates.get(matchId);
    }
    
    /**
     * Update the sound state for a match
     * 
     * @param matchId the match ID
     * @param match the match entity (for initial state)
     */
    public void updateState(Long matchId, Match match) {
        MatchSoundState state = matchSoundStates.computeIfAbsent(matchId, k -> new MatchSoundState());
        
        if (match.getActiveSound() != null) {
            state.setActiveSoundId(match.getActiveSound().getId());
            state.setActiveSoundTitle(match.getActiveSound().getTitle());
            state.setActiveSoundUrl(match.getActiveSound().getSoundUrl());
            state.setActiveSoundImageUrl(match.getActiveSound().getSoundImageUrl());
        } else {
            state.setActiveSoundId(null);
            state.setActiveSoundTitle(null);
            state.setActiveSoundUrl(null);
            state.setActiveSoundImageUrl(null);
        }
        
        state.setSoundStatus(match.getSoundStatus());
        state.setCurrentMillisecond(match.getCurrentMillisecond());
        state.setUpdatedAt(new Date());
        
        logger.debug("Updated state for match {}: sound={}, status={}, position={}", 
                matchId, state.getActiveSoundId(), state.getSoundStatus(), state.getCurrentMillisecond());
    }
    
    /**
     * Start playing a sound for a match
     * 
     * @param matchId the match ID
     * @param sound the sound to play
     */
    public void startSound(Long matchId, Sound sound) {
        MatchSoundState state = matchSoundStates.computeIfAbsent(matchId, k -> new MatchSoundState());
        
        state.setActiveSoundId(sound.getId());
        state.setActiveSoundTitle(sound.getTitle());
        state.setActiveSoundUrl(sound.getSoundUrl());
        state.setActiveSoundImageUrl(sound.getSoundImageUrl());
        state.setSoundStatus(SoundStatus.STARTED);
        state.setCurrentMillisecond(0L);
        state.setUpdatedAt(new Date());
        
        logger.info("Started sound {} for match {}", sound.getId(), matchId);
    }
    
    /**
     * Start playing a sound for a match
     * 
     * @param matchId the match ID
     * @param soundId the sound ID
     * @param title the sound title
     * @param soundUrl the sound URL
     * @param soundImageUrl the sound image URL
     */
    public void startSound(Long matchId, Long soundId, String title, String soundUrl, String soundImageUrl) {
        MatchSoundState state = matchSoundStates.computeIfAbsent(matchId, k -> new MatchSoundState());
        
        state.setActiveSoundId(soundId);
        state.setActiveSoundTitle(title);
        state.setActiveSoundUrl(soundUrl);
        state.setActiveSoundImageUrl(soundImageUrl);
        state.setSoundStatus(SoundStatus.STARTED);
        state.setCurrentMillisecond(0L);
        state.setUpdatedAt(new Date());
        
        logger.info("Started sound {} for match {}", soundId, matchId);
    }
    
    /**
     * Stop playing a sound for a match
     * 
     * @param matchId the match ID
     */
    public void stopSound(Long matchId) {
        MatchSoundState state = matchSoundStates.get(matchId);
        if (state != null) {
            state.setSoundStatus(SoundStatus.STOPPED);
            state.setUpdatedAt(new Date());
            
            logger.info("Stopped sound for match {}", matchId);
        }
    }
    
    /**
     * Pause playing a sound for a match
     * 
     * @param matchId the match ID
     * @param currentPosition the current position in milliseconds
     */
    public void pauseSound(Long matchId, Long currentPosition) {
        MatchSoundState state = matchSoundStates.get(matchId);
        if (state != null) {
            state.setSoundStatus(SoundStatus.PAUSED);
            state.setCurrentMillisecond(currentPosition);
            state.setUpdatedAt(new Date());
            
            logger.info("Paused sound for match {} at position {}", matchId, currentPosition);
        }
    }
    
    /**
     * Resume playing a sound for a match
     * 
     * @param matchId the match ID
     */
    public void resumeSound(Long matchId) {
        MatchSoundState state = matchSoundStates.get(matchId);
        if (state != null) {
            state.setSoundStatus(SoundStatus.STARTED);
            state.setUpdatedAt(new Date());
            
            logger.info("Resumed sound for match {} from position {}", matchId, state.getCurrentMillisecond());
        }
    }
    
    /**
     * Seek to a position in the sound for a match
     * 
     * @param matchId the match ID
     * @param position the position in milliseconds
     */
    public void seekSound(Long matchId, Long position) {
        MatchSoundState state = matchSoundStates.get(matchId);
        if (state != null) {
            state.setCurrentMillisecond(position);
            state.setUpdatedAt(new Date());
            
            logger.info("Seeked sound for match {} to position {}", matchId, position);
        }
    }
    
    /**
     * Update the current position for a match sound
     * This is called by the timer service
     * 
     * @param matchId the match ID
     * @param position the current position in milliseconds
     */
    public void updatePosition(Long matchId, Long position) {
        MatchSoundState state = matchSoundStates.get(matchId);
        if (state != null && state.getSoundStatus() == SoundStatus.STARTED) {
            state.setCurrentMillisecond(position);
            // Don't update the timestamp for timer updates
        }
    }
    
    /**
     * Inner class to represent a match sound state
     */
    public static class MatchSoundState {
        private Long activeSoundId;
        private String activeSoundTitle;
        private String activeSoundUrl;
        private String activeSoundImageUrl;
        private SoundStatus soundStatus = SoundStatus.STOPPED;
        private Long currentMillisecond = 0L;
        private Date updatedAt = new Date();
        
        public Long getActiveSoundId() {
            return activeSoundId;
        }
        
        public void setActiveSoundId(Long activeSoundId) {
            this.activeSoundId = activeSoundId;
        }
        
        public String getActiveSoundTitle() {
            return activeSoundTitle;
        }
        
        public void setActiveSoundTitle(String activeSoundTitle) {
            this.activeSoundTitle = activeSoundTitle;
        }
        
        public String getActiveSoundUrl() {
            return activeSoundUrl;
        }
        
        public void setActiveSoundUrl(String activeSoundUrl) {
            this.activeSoundUrl = activeSoundUrl;
        }
        
        public String getActiveSoundImageUrl() {
            return activeSoundImageUrl;
        }
        
        public void setActiveSoundImageUrl(String activeSoundImageUrl) {
            this.activeSoundImageUrl = activeSoundImageUrl;
        }
        
        public SoundStatus getSoundStatus() {
            return soundStatus;
        }
        
        public void setSoundStatus(SoundStatus soundStatus) {
            this.soundStatus = soundStatus;
        }
        
        public Long getCurrentMillisecond() {
            return currentMillisecond;
        }
        
        public void setCurrentMillisecond(Long currentMillisecond) {
            this.currentMillisecond = currentMillisecond;
        }
        
        public Date getUpdatedAt() {
            return updatedAt;
        }
        
        public void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
