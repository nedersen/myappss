package com.project.fanla.repository;

import com.project.fanla.model.entity.Match;
import com.project.fanla.model.entity.MatchSoundDetail;
import com.project.fanla.model.entity.Sound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchSoundDetailRepository extends JpaRepository<MatchSoundDetail, Long> {
    
    List<MatchSoundDetail> findByMatchOrderByCreatedAtDesc(Match match);
    
    List<MatchSoundDetail> findByMatchAndActiveSoundOrderByCreatedAtDesc(Match match, Sound sound);
    
    @Query("SELECT msd FROM MatchSoundDetail msd WHERE msd.match.id = :matchId ORDER BY msd.createdAt DESC")
    List<MatchSoundDetail> findByMatchIdOrderByCreatedAtDesc(Long matchId);
    
    @Query("SELECT msd FROM MatchSoundDetail msd WHERE msd.match.id = :matchId ORDER BY msd.createdAt DESC LIMIT 1")
    Optional<MatchSoundDetail> findLatestByMatchId(Long matchId);
    
    @Query("SELECT msd FROM MatchSoundDetail msd WHERE msd.match.team.id = :teamId ORDER BY msd.createdAt DESC")
    List<MatchSoundDetail> findByTeamIdOrderByCreatedAtDesc(Long teamId);
    
    /**
     * Find all match sound details that reference a specific sound as active sound
     * 
     * @param sound the sound to find references for
     * @return list of match sound details that reference the sound
     */
    List<MatchSoundDetail> findByActiveSound(Sound sound);
}
