package com.project.fanla.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for Lyrics
 */
public class LyricsRequest {
    
    @NotBlank(message = "Lyric text cannot be blank")
    private String lyric;
    
    @NotNull(message = "Second cannot be null")
    @Min(value = 0, message = "Second must be greater than or equal to 0")
    private Integer second;
    
    // Constructors
    public LyricsRequest() {
    }
    
    public LyricsRequest(String lyric, Integer second) {
        this.lyric = lyric;
        this.second = second;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "LyricsRequest{" +
                "lyric='" + lyric + '\'' +
                ", second=" + second +
                '}';
    }
}
