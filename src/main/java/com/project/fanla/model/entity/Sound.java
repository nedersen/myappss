package com.project.fanla.model.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.project.fanla.model.enums.SoundStatus;

import java.util.Date;

@Entity
@Table(name = "sounds")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Sound {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "sound_url", columnDefinition = "TEXT")
    private String soundUrl;
    
    @Column(name = "sound_image_url", columnDefinition = "TEXT")
    private String soundImageUrl;
    
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private Team team;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SoundStatus status;
    
    @Column(name = "current_millisecond")
    private Long currentMillisecond;
    
    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
    
    // Constructors
    public Sound() {
    }
    
    public Sound(String title, String soundUrl, String soundImageUrl, Team team, SoundStatus status, Long currentMillisecond) {
        this.title = title;
        this.soundUrl = soundUrl;
        this.soundImageUrl = soundImageUrl;
        this.team = team;
        this.status = status;
        this.currentMillisecond = currentMillisecond;
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
    
    public Team getTeam() {
        return team;
    }
    
    public void setTeam(Team team) {
        this.team = team;
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
