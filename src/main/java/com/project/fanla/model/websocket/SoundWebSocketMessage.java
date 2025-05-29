package com.project.fanla.model.websocket;

import com.project.fanla.model.dto.LyricsDto;
import com.project.fanla.model.enums.SoundStatus;
import java.util.Date;
import java.util.List;

public class SoundWebSocketMessage {
    private Long matchId;
    private Long soundId;
    private String title;
    private String soundUrl;
    private String soundImageUrl;
    private SoundStatus status;
    private Long currentMillisecond;
    private Date updatedAt;
    private List<LyricsDto> lyrics;

    public SoundWebSocketMessage() {
    }

    public SoundWebSocketMessage(Long matchId, Long soundId, String title, String soundUrl, 
                                String soundImageUrl, SoundStatus status, Long currentMillisecond) {
        this.matchId = matchId;
        this.soundId = soundId;
        this.title = title;
        this.soundUrl = soundUrl;
        this.soundImageUrl = soundImageUrl;
        this.status = status;
        this.currentMillisecond = currentMillisecond;
        this.updatedAt = new Date();
        this.lyrics = null;
    }
    
    public SoundWebSocketMessage(Long matchId, Long soundId, String title, String soundUrl, 
                                String soundImageUrl, SoundStatus status, Long currentMillisecond,
                                List<LyricsDto> lyrics) {
        this.matchId = matchId;
        this.soundId = soundId;
        this.title = title;
        this.soundUrl = soundUrl;
        this.soundImageUrl = soundImageUrl;
        this.status = status;
        this.currentMillisecond = currentMillisecond;
        this.updatedAt = new Date();
        this.lyrics = lyrics;
    }

    // Getter ve Setter metotları
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<LyricsDto> getLyrics() {
        return lyrics;
    }
    
    public void setLyrics(List<LyricsDto> lyrics) {
        this.lyrics = lyrics;
    }
}
