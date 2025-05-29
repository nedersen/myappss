package com.project.fanla.repository;

import com.project.fanla.model.entity.Lyrics;
import com.project.fanla.model.entity.Sound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LyricsRepository extends JpaRepository<Lyrics, Long> {
    
    /**
     * Find all lyrics by sound
     * 
     * @param sound the sound entity
     * @return list of lyrics for the sound
     */
    List<Lyrics> findBySound(Sound sound);
    
    /**
     * Find all lyrics by sound id
     * 
     * @param soundId the sound id
     * @return list of lyrics for the sound
     */
    List<Lyrics> findBySoundId(Long soundId);
    
    /**
     * Find all lyrics by sound id ordered by second
     * 
     * @param soundId the sound id
     * @return list of lyrics for the sound ordered by second
     */
    List<Lyrics> findBySoundIdOrderBySecondAsc(Long soundId);
    
    /**
     * Delete all lyrics by sound
     * 
     * @param sound the sound entity
     */
    void deleteBySound(Sound sound);
    
    /**
     * Delete all lyrics by sound id
     * 
     * @param soundId the sound id
     */
    void deleteBySoundId(Long soundId);
}
