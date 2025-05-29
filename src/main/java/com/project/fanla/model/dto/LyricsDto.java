package com.project.fanla.model.dto;

/**
 * Data Transfer Object for Lyrics entity
 * Used for sending lyrics data over WebSocket
 */
public class LyricsDto {
    private Long id;
    private String lyric;
    private Integer second;
    
    public LyricsDto() {
    }
    
    public LyricsDto(Long id, String lyric, Integer second) {
        this.id = id;
        this.lyric = lyric;
        this.second = second;
    }
    
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
}
