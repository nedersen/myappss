package com.project.fanla.payload.response;

import com.project.fanla.model.entity.Lyrics;
import java.time.LocalDateTime;

/**
 * Response DTO for Lyrics
 */
public class LyricsResponse {
    
    private Long id;
    private String lyric;
    private Integer second;
    private Long soundId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public LyricsResponse() {
    }
    
    public LyricsResponse(Long id, String lyric, Integer second, Long soundId, 
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.lyric = lyric;
        this.second = second;
        this.soundId = soundId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    /**
     * Create a response from a Lyrics entity
     * 
     * @param lyrics the lyrics entity
     * @return the response DTO
     */
    public static LyricsResponse fromEntity(Lyrics lyrics) {
        return new LyricsResponse(
            lyrics.getId(),
            lyrics.getLyric(),
            lyrics.getSecond(),
            lyrics.getSound().getId(),
            lyrics.getCreatedAt(),
            lyrics.getUpdatedAt()
        );
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLyric() {
        return lyric;
    }
    
    public void setLyric(String lyric) {
        this.lyric = lyric;
    }
    
    public Integer getSecond() {
        return second;
    }
    
    public void setSecond(Integer second) {
        this.second = second;
    }
    
    public Long getSoundId() {
        return soundId;
    }
    
    public void setSoundId(Long soundId) {
        this.soundId = soundId;
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
