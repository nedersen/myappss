package com.project.fanla.payload.response;

import com.project.fanla.model.entity.Sound;
import com.project.fanla.model.enums.SoundStatus;

import java.util.Date;

public class SoundResponse {
    
    private Long id;
    private String title;
    private String soundUrl;
    private String soundImageUrl;
    private Long teamId;
    private String teamName;
    private SoundStatus status;
    private Long currentMillisecond;
    private Date updatedAt;
    
    // Constructors
    public SoundResponse() {
    }
    
    public SoundResponse(Sound sound) {
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
}
