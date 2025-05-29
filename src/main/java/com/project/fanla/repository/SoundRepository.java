package com.project.fanla.repository;

import com.project.fanla.model.entity.Sound;
import com.project.fanla.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoundRepository extends JpaRepository<Sound, Long> {
    List<Sound> findByTeam(Team team);
    List<Sound> findByTitleContainingIgnoreCase(String title);
    List<Sound> findByTeamAndTitleContainingIgnoreCase(Team team, String title);
}
