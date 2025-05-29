package com.project.fanla.payload.request;

import com.project.fanla.model.enums.SoundStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SoundRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String soundUrl;
    
    private String soundImageUrl;
    
    private SoundStatus status = SoundStatus.STOPPED;
    
    private Long currentMillisecond = 0L;
    
    // Constructors
    public SoundRequest() {
    }
    
    public SoundRequest(String title, String soundUrl, String soundImageUrl, 
                       SoundStatus status, Long currentMillisecond) {
        this.title = title;
        this.soundUrl = soundUrl;
        this.soundImageUrl = soundImageUrl;
        this.status = status;
        this.currentMillisecond = currentMillisecond;
    }
    
    // Getters and Setters
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
}
