package com.project.fanla.repository;

import com.project.fanla.model.entity.Role;
import com.project.fanla.model.entity.Team;
import com.project.fanla.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    
    // Admin user management methods
    List<User> findByRole(Role role);
    Optional<User> findByIdAndRole(Long id, Role role);
    List<User> findByUsernameContainingIgnoreCaseAndRole(String username, Role role);
    List<User> findByTeamAndRole(Team team, Role role);
    List<User> findByIsActiveAndRole(Boolean isActive, Role role);
}
