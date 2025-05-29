package com.project.fanla.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.fanla.model.enums.MatchStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Request DTO for Match
 */
public class MatchRequest {
    
    private Long opponentTeamId;
    
    private String manualOpponentName;
    
    private String manualOpponentLogo;
    
    @NotNull(message = "Match status cannot be null")
    private MatchStatus status;
    
    private Integer homeScore = 0;
    
    private Integer awayScore = 0;
    
    @NotNull(message = "Match date cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime matchDate;
    
    // Constructors
    public MatchRequest() {
    }
    
    public MatchRequest(Long opponentTeamId, String manualOpponentName, String manualOpponentLogo,
                       MatchStatus status, Integer homeScore, Integer awayScore, LocalDateTime matchDate) {
        this.opponentTeamId = opponentTeamId;
        this.manualOpponentName = manualOpponentName;
        this.manualOpponentLogo = manualOpponentLogo;
        this.status = status;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.matchDate = matchDate;
    }
    
    // Getters and Setters
    public Long getOpponentTeamId() {
        return opponentTeamId;
    }
    
    public void setOpponentTeamId(Long opponentTeamId) {
        this.opponentTeamId = opponentTeamId;
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
    
    public MatchStatus getStatus() {
        return status;
    }
    
    public void setStatus(MatchStatus status) {
        this.status = status;
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
    
    /**
     * Validate if the request has valid opponent information
     * Either opponentTeamId or manualOpponentName must be provided
     * 
     * @return true if valid, false otherwise
     */
    public boolean hasValidOpponent() {
        return opponentTeamId != null || (manualOpponentName != null && !manualOpponentName.trim().isEmpty());
    }
    
    @Override
    public String toString() {
        return "MatchRequest{" +
                "opponentTeamId=" + opponentTeamId +
                ", manualOpponentName='" + manualOpponentName + '\'' +
                ", manualOpponentLogo='" + manualOpponentLogo + '\'' +
                ", status=" + status +
                ", homeScore=" + homeScore +
                ", awayScore=" + awayScore +
                ", matchDate=" + matchDate +
                '}';
    }
}
