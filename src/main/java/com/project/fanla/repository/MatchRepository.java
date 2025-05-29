package com.project.fanla.repository;

import com.project.fanla.model.entity.Match;
import com.project.fanla.model.entity.Team;
import com.project.fanla.model.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    /**
     * Find all matches by team
     * 
     * @param team the team
     * @return list of matches for the team
     */
    List<Match> findByTeam(Team team);
    
    /**
     * Find all matches by team id
     * 
     * @param teamId the team id
     * @return list of matches for the team
     */
    List<Match> findByTeamId(Long teamId);
    
    /**
     * Find all matches by team ordered by match date descending
     * 
     * @param team the team
     * @return list of matches for the team ordered by match date
     */
    List<Match> findByTeamOrderByMatchDateDesc(Team team);
    
    /**
     * Find all matches by team id ordered by match date descending
     * 
     * @param teamId the team id
     * @return list of matches for the team ordered by match date
     */
    List<Match> findByTeamIdOrderByMatchDateDesc(Long teamId);
    
    /**
     * Find all matches by team and status
     * 
     * @param team the team
     * @param status the match status
     * @return list of matches for the team with the specified status
     */
    List<Match> findByTeamAndStatus(Team team, MatchStatus status);
    
    /**
     * Find all matches by team id and status
     * 
     * @param teamId the team id
     * @param status the match status
     * @return list of matches for the team with the specified status
     */
    List<Match> findByTeamIdAndStatus(Long teamId, MatchStatus status);
    
    /**
     * Find all matches by team and match date after
     * 
     * @param team the team
     * @param date the date
     * @return list of matches for the team after the specified date
     */
    List<Match> findByTeamAndMatchDateAfter(Team team, LocalDateTime date);
    
    /**
     * Find all matches by team id and match date after
     * 
     * @param teamId the team id
     * @param date the date
     * @return list of matches for the team after the specified date
     */
    List<Match> findByTeamIdAndMatchDateAfter(Long teamId, LocalDateTime date);
    
    /**
     * Find all matches by team and match date before
     * 
     * @param team the team
     * @param date the date
     * @return list of matches for the team before the specified date
     */
    List<Match> findByTeamAndMatchDateBefore(Team team, LocalDateTime date);
    
    /**
     * Find all matches by team id and match date before
     * 
     * @param teamId the team id
     * @param date the date
     * @return list of matches for the team before the specified date
     */
    List<Match> findByTeamIdAndMatchDateBefore(Long teamId, LocalDateTime date);
    
    /**
     * Find all matches by team and opponent team
     * 
     * @param team the team
     * @param opponentTeam the opponent team
     * @return list of matches between the team and opponent team
     */
    List<Match> findByTeamAndOpponentTeam(Team team, Team opponentTeam);
    
    /**
     * Find all matches by team id and opponent team id
     * 
     * @param teamId the team id
     * @param opponentTeamId the opponent team id
     * @return list of matches between the team and opponent team
     */
    List<Match> findByTeamIdAndOpponentTeamId(Long teamId, Long opponentTeamId);
    
    /**
     * Find all matches by team and manual opponent name containing
     * 
     * @param team the team
     * @param opponentName the opponent name
     * @return list of matches for the team with manual opponent name containing the specified string
     */
    List<Match> findByTeamAndManualOpponentNameContainingIgnoreCase(Team team, String opponentName);
    
    /**
     * Find all matches by team id and manual opponent name containing
     * 
     * @param teamId the team id
     * @param opponentName the opponent name
     * @return list of matches for the team with manual opponent name containing the specified string
     */
    List<Match> findByTeamIdAndManualOpponentNameContainingIgnoreCase(Long teamId, String opponentName);
    
    /**
     * Find all matches by team id and active sound id
     * 
     * @param teamId the team id
     * @param activeSoundId the active sound id
     * @return list of matches for the team with the specified active sound
     */
    List<Match> findByTeamIdAndActiveSound_Id(Long teamId, Long activeSoundId);
}
