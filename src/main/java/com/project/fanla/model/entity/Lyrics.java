package com.project.fanla.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lyrics")
public class Lyrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String lyric;
    
    @Column(nullable = false)
    private Integer second;
    
    @ManyToOne
    @JoinColumn(name = "sound_id", nullable = false)
    private Sound sound;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Lyrics() {
    }
    
    public Lyrics(String lyric, Integer second, Sound sound) {
        this.lyric = lyric;
        this.second = second;
        this.sound = sound;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
    
    public Sound getSound() {
        return sound;
    }
    
    public void setSound(Sound sound) {
        this.sound = sound;
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
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
