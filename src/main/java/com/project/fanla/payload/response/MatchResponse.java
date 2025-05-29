package com.project.fanla.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.fanla.model.entity.Match;
import com.project.fanla.model.entity.Team;
import com.project.fanla.model.enums.MatchStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for Match
 */
public class MatchResponse {
    
    private Long id;
    private Long teamId;
    private String teamName;
    private String teamLogo;
    private Long opponentTeamId;
    private String opponentTeamName;
    private String opponentTeamLogo;
    private String manualOpponentName;
    private String manualOpponentLogo;
    private MatchStatus status;
    private Integer homeScore;
    private Integer awayScore;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime matchDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;
    
    // Constructors
    public MatchResponse() {
    }
    
    public MatchResponse(Long id, Long teamId, String teamName, String teamLogo, Long opponentTeamId, 
                        String opponentTeamName, String opponentTeamLogo, String manualOpponentName, 
                        String manualOpponentLogo, MatchStatus status, Integer homeScore, 
                        Integer awayScore, LocalDateTime matchDate, LocalDateTime createdAt) {
        this.id = id;
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamLogo = teamLogo;
        this.opponentTeamId = opponentTeamId;
        this.opponentTeamName = opponentTeamName;
        this.opponentTeamLogo = opponentTeamLogo;
        this.manualOpponentName = manualOpponentName;
        this.manualOpponentLogo = manualOpponentLogo;
        this.status = status;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.matchDate = matchDate;
        this.createdAt = createdAt;
    }
    
    /**
     * Create a response from a Match entity
     * 
     * @param match the match entity
     * @return the response DTO
     */
    public static MatchResponse fromEntity(Match match) {
        Team team = match.getTeam();
        Team opponentTeam = match.getOpponentTeam();
        
        return new MatchResponse(
            match.getId(),
            team.getId(),
            team.getName(),
            team.getLogoUrl(),
            opponentTeam != null ? opponentTeam.getId() : null,
            opponentTeam != null ? opponentTeam.getName() : null,
            opponentTeam != null ? opponentTeam.getLogoUrl() : null,
            match.getManualOpponentName(),
            match.getManualOpponentLogo(),
            match.getStatus(),
            match.getHomeScore(),
            match.getAwayScore(),
            match.getMatchDate(),
            match.getCreatedAt()
        );
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTeamId() {
        return teamId;
    }
    
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
    
    public String getTeamName() {
        return teamName;
    }
    
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    
    public String getTeamLogo() {
        return teamLogo;
    }
    
    public void setTeamLogo(String teamLogo) {
        this.teamLogo = teamLogo;
    }
    
    public Long getOpponentTeamId() {
        return opponentTeamId;
    }
    
    public void setOpponentTeamId(Long opponentTeamId) {
        this.opponentTeamId = opponentTeamId;
    }
    
    public String getOpponentTeamName() {
        return opponentTeamName;
    }
    
    public void setOpponentTeamName(String opponentTeamName) {
        this.opponentTeamName = opponentTeamName;
    }
    
    public String getOpponentTeamLogo() {
        return opponentTeamLogo;
    }
    
    public void setOpponentTeamLogo(String opponentTeamLogo) {
        this.opponentTeamLogo = opponentTeamLogo;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
