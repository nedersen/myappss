package com.project.fanla.model.entity;

// import com.project.fanla.model.enums.EventType;
import com.project.fanla.model.enums.SoundStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_sound_details")
public class MatchSoundDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;
    
    @ManyToOne
    @JoinColumn(name = "active_sound_id")
    private Sound activeSound;
    
    @Column(name = "current_millisecond")
    private Long currentMillisecond;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sound_status")
    private SoundStatus soundStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private com.project.fanla.model.enums.EventType eventType;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public MatchSoundDetail() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public MatchSoundDetail(Match match, Sound activeSound, Long currentMillisecond, 
                           SoundStatus soundStatus, com.project.fanla.model.enums.EventType eventType) {
        this.match = match;
        this.activeSound = activeSound;
        this.currentMillisecond = currentMillisecond;
        this.soundStatus = soundStatus;
        this.eventType = eventType;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Match getMatch() {
        return match;
    }
    
    public void setMatch(Match match) {
        this.match = match;
    }
    
    public Sound getActiveSound() {
        return activeSound;
    }
    
    public void setActiveSound(Sound activeSound) {
        this.activeSound = activeSound;
    }
    
    public Long getCurrentMillisecond() {
        return currentMillisecond;
    }
    
    public void setCurrentMillisecond(Long currentMillisecond) {
        this.currentMillisecond = currentMillisecond;
    }
    
    public SoundStatus getSoundStatus() {
        return soundStatus;
    }
    
    public void setSoundStatus(SoundStatus soundStatus) {
        this.soundStatus = soundStatus;
    }
    
    public com.project.fanla.model.enums.EventType getEventType() {
        return eventType;
    }
    
    public void setEventType(com.project.fanla.model.enums.EventType eventType) {
        this.eventType = eventType;
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
