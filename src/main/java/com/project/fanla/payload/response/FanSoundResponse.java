package com.project.fanla.payload.response;

import com.project.fanla.model.entity.Sound;
import com.project.fanla.model.enums.SoundStatus;

import java.util.Date;

/**
 * Response DTO for Sound entity for fan-facing APIs
 */
public class FanSoundResponse {
    
    private Long id;
    private String title;
    private String soundUrl;
    private String soundImageUrl;
    private Long teamId;
    private String teamName;
    private SoundStatus status;
    private Long currentMillisecond;
    private Date updatedAt;
    private Boolean isDownloaded; // Indicates if the sound is downloaded on the client device
    
    // Constructors
    public FanSoundResponse() {
    }
    
    public FanSoundResponse(Sound sound) {
        this.id = sound.getId();
        this.title = sound.getTitle();
        this.soundUrl = sound.getSoundUrl();
        this.soundImageUrl = sound.getSoundImageUrl();
        
        if (sound.getTeam() != null) {
            this.teamId = sound.getTeam().getId();
            this.teamName = sound.getTeam().getName();
        }
        
        this.status = sound.getStatus();
        this.currentMillisecond = sound.getCurrentMillisecond();
        this.updatedAt = sound.getUpdatedAt();
        this.isDownloaded = false; // Default value, will be updated by client
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSoundUrl() {
        return soundUrl;
    }
    
    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }
    
    public String getSoundImageUrl() {
        return soundImageUrl;
    }
    
    public void setSoundImageUrl(String soundImageUrl) {
        this.soundImageUrl = soundImageUrl;
    }
    
    public Long getTeamId() {
        return teamId;
    }
    
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
    
    public String getTeamName() {
        return teamName;
    }
    
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    
    public SoundStatus getStatus() {
        return status;
    }
    
    public void setStatus(SoundStatus status) {
        this.status = status;
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
    
    public Boolean getIsDownloaded() {
        return isDownloaded;
    }
    
    public void setIsDownloaded(Boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }
}
