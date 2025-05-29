package com.project.fanla.model.entity;

import jakarta.persistence.*;
import com.project.fanla.model.enums.MatchStatus;
import com.project.fanla.model.enums.SoundStatus;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "matches")
public class Match {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    
    @ManyToOne
    @JoinColumn(name = "opponent_team_id")
    private Team opponentTeam;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MatchStatus status;

    @Column(name = "manual_opponent_name")
    private String manualOpponentName;

    @Column(name = "manual_opponent_logo")
    private String manualOpponentLogo;

    @Column(name = "home_score")
    private Integer homeScore = 0;

    @Column(name = "away_score")
    private Integer awayScore = 0;
    
    @Column(name = "match_date", nullable = false)
    private LocalDateTime matchDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Sound related fields
    @ManyToOne
    @JoinColumn(name = "active_sound_id")
    private Sound activeSound;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sound_status")
    private SoundStatus soundStatus = SoundStatus.STOPPED;
    
    @Column(name = "current_millisecond")
    private Long currentMillisecond = 0L;
    
    @Column(name = "sound_updated_at")
    private Date soundUpdatedAt;
    
    // Constructors
    public Match() {
    }
    
    public Match(Team team, Team opponentTeam, MatchStatus status, String manualOpponentName, 
                String manualOpponentLogo, Integer homeScore, Integer awayScore, LocalDateTime matchDate) {
        this.team = team;
        this.opponentTeam = opponentTeam;
        this.status = status;
        this.manualOpponentName = manualOpponentName;
        this.manualOpponentLogo = manualOpponentLogo;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.matchDate = matchDate;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Team getTeam() {
        return team;
    }
    
    public void setTeam(Team team) {
        this.team = team;
    }
    
    public Team getOpponentTeam() {
        return opponentTeam;
    }
    
    public void setOpponentTeam(Team opponentTeam) {
        this.opponentTeam = opponentTeam;
    }
    
    public MatchStatus getStatus() {
        return status;
    }
    
    public void setStatus(MatchStatus status) {
        this.status = status;
    }
    
    public String getManualOpponentName() {
        return manualOpponentName;
    }
    
    public void setManualOpponentName(String manualOpponentName) {
        this.manualOpponentName = manualOpponentName;
    }
    
    public String getManualOpponentLogo() {
        return manualOpponentLogo;
    }
    
    public void setManualOpponentLogo(String manualOpponentLogo) {
        this.manualOpponentLogo = manualOpponentLogo;
    }
    
    public Integer getHomeScore() {
        return homeScore;
    }
    
    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }
    
    public Integer getAwayScore() {
        return awayScore;
    }
    
    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }
    
    public LocalDateTime getMatchDate() {
        return matchDate;
    }
    
    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Sound related getters and setters
    public Sound getActiveSound() {
        return activeSound;
    }
    
    public void setActiveSound(Sound activeSound) {
        this.activeSound = activeSound;
    }
    
    public SoundStatus getSoundStatus() {
        return soundStatus;
    }
    
    public void setSoundStatus(SoundStatus soundStatus) {
        this.soundStatus = soundStatus;
    }
    
    public Long getCurrentMillisecond() {
        return currentMillisecond;
    }
    
    public void setCurrentMillisecond(Long currentMillisecond) {
        this.currentMillisecond = currentMillisecond;
    }
    
    public Date getSoundUpdatedAt() {
        return soundUpdatedAt;
    }
    
    public void setSoundUpdatedAt(Date soundUpdatedAt) {
        this.soundUpdatedAt = soundUpdatedAt;
    }
    
    @PreUpdate
    @PrePersist
    protected void updateSoundTimestamp() {
        if (soundStatus != null) {
            soundUpdatedAt = new Date();
        }
    }
}
