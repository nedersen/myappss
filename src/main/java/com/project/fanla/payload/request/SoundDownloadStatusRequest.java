package com.project.fanla.payload.request;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for checking sound download status
 */
public class SoundDownloadStatusRequest {
    
    private List<Long> soundIds;
    private Map<Long, Boolean> downloadStatus; // Map of sound ID to download status
    
    // Constructors
    public SoundDownloadStatusRequest() {
    }
    
    public SoundDownloadStatusRequest(List<Long> soundIds, Map<Long, Boolean> downloadStatus) {
        this.soundIds = soundIds;
        this.downloadStatus = downloadStatus;
    }
    
    // Getters and Setters
    public List<Long> getSoundIds() {
        return soundIds;
    }
    
    public void setSoundIds(List<Long> soundIds) {
        this.soundIds = soundIds;
    }
    
    public Map<Long, Boolean> getDownloadStatus() {
        return downloadStatus;
    }
    
    public void setDownloadStatus(Map<Long, Boolean> downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
}
