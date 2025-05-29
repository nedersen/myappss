package com.project.fanla.repository;

import com.project.fanla.model.entity.Country;
import com.project.fanla.model.entity.Team;
import com.project.fanla.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);
    List<Team> findByNameContainingIgnoreCase(String name);
    List<Team> findByCountry(Country country);
    List<Team> findByCreatedBy(User user);
    boolean existsByName(String name);
    List<Team> findByIsActive(Boolean isActive);
}
