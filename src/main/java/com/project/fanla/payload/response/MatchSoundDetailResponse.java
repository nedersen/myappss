package com.project.fanla.payload.response;

import com.project.fanla.model.entity.MatchSoundDetail;
// import com.project.fanla.model.enums.EventType;
import com.project.fanla.model.enums.SoundStatus;

import java.time.LocalDateTime;

public class MatchSoundDetailResponse {
    
    private Long id;
    private Long matchId;
    private String matchName;
    private Long activeSoundId;
    private String activeSoundTitle;
    private String activeSoundUrl;
    private Long currentMillisecond;
    private SoundStatus soundStatus;
    private com.project.fanla.model.enums.EventType eventType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public MatchSoundDetailResponse() {
    }
    
    public MatchSoundDetailResponse(Long id, Long matchId, String matchName, Long activeSoundId, 
                             String activeSoundTitle, String activeSoundUrl, Long currentMillisecond, 
                             SoundStatus soundStatus, com.project.fanla.model.enums.EventType eventType, 
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.matchId = matchId;
        this.matchName = matchName;
        this.activeSoundId = activeSoundId;
        this.activeSoundTitle = activeSoundTitle;
        this.activeSoundUrl = activeSoundUrl;
        this.currentMillisecond = currentMillisecond;
        this.soundStatus = soundStatus;
        this.eventType = eventType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Factory method to create from entity
    public static MatchSoundDetailResponse fromEntity(MatchSoundDetail matchSoundDetail) {
        String matchName = "";
        if (matchSoundDetail.getMatch() != null) {
            if (matchSoundDetail.getMatch().getOpponentTeam() != null) {
                matchName = matchSoundDetail.getMatch().getTeam().getName() + " vs " + 
                           matchSoundDetail.getMatch().getOpponentTeam().getName();
            } else if (matchSoundDetail.getMatch().getManualOpponentName() != null) {
                matchName = matchSoundDetail.getMatch().getTeam().getName() + " vs " + 
                           matchSoundDetail.getMatch().getManualOpponentName();
            }
        }
        
        return new MatchSoundDetailResponse(
            matchSoundDetail.getId(),
            matchSoundDetail.getMatch() != null ? matchSoundDetail.getMatch().getId() : null,
            matchName,
            matchSoundDetail.getActiveSound() != null ? matchSoundDetail.getActiveSound().getId() : null,
            matchSoundDetail.getActiveSound() != null ? matchSoundDetail.getActiveSound().getTitle() : null,
            matchSoundDetail.getActiveSound() != null ? matchSoundDetail.getActiveSound().getSoundUrl() : null,
            matchSoundDetail.getCurrentMillisecond(),
            matchSoundDetail.getSoundStatus(),
            matchSoundDetail.getEventType(),
            matchSoundDetail.getCreatedAt(),
            matchSoundDetail.getUpdatedAt()
        );
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getMatchId() {
        return matchId;
    }
    
    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
    
    public String getMatchName() {
        return matchName;
    }
    
    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }
    
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
    
    public Long getCurrentMillisecond() {
        return currentMillisecond;
    }
    
    public void setCurrentMillisecond(Long currentMillisecond) {
        this.currentMillisecond = currentMillisecond;
    }
    
    public SoundStatus getSoundStatus() {
        return soundStatus;
    }
    
    public void setSoundStatus(SoundStatus soundStatus) {
        this.soundStatus = soundStatus;
    }
    
    public com.project.fanla.model.enums.EventType getEventType() {
        return eventType;
    }
    
    public void setEventType(com.project.fanla.model.enums.EventType eventType) {
        this.eventType = eventType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
