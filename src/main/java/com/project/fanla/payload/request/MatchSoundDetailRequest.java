package com.project.fanla.payload.request;

// import com.project.fanla.model.enums.EventType;
import com.project.fanla.model.enums.SoundStatus;
import jakarta.validation.constraints.NotNull;

public class MatchSoundDetailRequest {
    
    @NotNull(message = "Match ID cannot be null")
    private Long matchId;
    
    @NotNull(message = "Sound ID cannot be null")
    private Long soundId;
    
    @NotNull(message = "Event type cannot be null")
    private com.project.fanla.model.enums.EventType eventType;
    
    @NotNull(message = "Sound status cannot be null")
    private SoundStatus soundStatus;
    
    private Long currentMillisecond;
    
    // Constructors
    public MatchSoundDetailRequest() {
    }
    
    public MatchSoundDetailRequest(Long matchId, Long soundId, com.project.fanla.model.enums.EventType eventType, 
                                  SoundStatus soundStatus, Long currentMillisecond) {
        this.matchId = matchId;
        this.soundId = soundId;
        this.eventType = eventType;
        this.soundStatus = soundStatus;
        this.currentMillisecond = currentMillisecond;
    }
    
    // Getters and Setters
    public Long getMatchId() {
        return matchId;
    }
    
    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
    
    public Long getSoundId() {
        return soundId;
    }
    
    public void setSoundId(Long soundId) {
        this.soundId = soundId;
    }
    
    public com.project.fanla.model.enums.EventType getEventType() {
        return eventType;
    }
    
    public void setEventType(com.project.fanla.model.enums.EventType eventType) {
        this.eventType = eventType;
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
}
