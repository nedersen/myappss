package com.project.fanla.controller.superadmin;

import com.project.fanla.model.entity.Country;
import com.project.fanla.model.entity.SubscriptionType;
import com.project.fanla.model.entity.Team;
import com.project.fanla.model.entity.User;
import com.project.fanla.payload.response.SubscriptionTypeResponse;
import com.project.fanla.payload.request.TeamRequest;
import com.project.fanla.payload.response.TeamResponse;
import com.project.fanla.repository.CountryRepository;
import com.project.fanla.repository.SubscriptionTypeRepository;
import com.project.fanla.repository.TeamRepository;
import com.project.fanla.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/superadmin/teams")
@PreAuthorize("hasRole('ROLE_SuperAdmin')")
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private CountryRepository countryRepository;
    
    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;
    
    @Autowired
    private UserRepository userRepository;

    // Get all teams
    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        List<TeamResponse> teams = teamRepository.findAll().stream()
                .map(TeamResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(teams);
    }

    // Get team by ID
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable Long id) {
        return teamRepository.findById(id)
                .map(TeamResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new team
    @PostMapping
    public ResponseEntity<?> createTeam(@Valid @RequestBody TeamRequest teamRequest) {
        // Check if team with the same name already exists
        if (teamRepository.existsByName(teamRequest.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body(String.format("Team with name '%s' already exists", teamRequest.getName()));
        }

        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Get country
        Country country = countryRepository.findById(teamRequest.getCountryId())
                .orElseThrow(() -> new RuntimeException("Error: Country not found."));
        
        // Get subscription type if provided
        SubscriptionType subscriptionType = null;
        if (teamRequest.getSubscriptionTypeId() != null) {
            subscriptionType = subscriptionTypeRepository.findById(teamRequest.getSubscriptionTypeId())
                    .orElseThrow(() -> new RuntimeException("Error: Subscription type not found."));
        }

        // Create new team
        Team team = new Team();
        team.setName(teamRequest.getName());
        team.setLogoUrl(teamRequest.getLogoUrl());
        team.setStadiumName(teamRequest.getStadiumName());
        team.setStadiumLocation(teamRequest.getStadiumLocation());
        team.setCountry(country);
        team.setSubscriptionType(subscriptionType);
        team.setCreatedBy(currentUser);
        team.setIsActive(teamRequest.getIsActive());
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());
        
        // Set subscription dates if subscription type is provided
        if (subscriptionType != null) {
            LocalDateTime now = LocalDateTime.now();
            team.setSubscriptionStart(now);
            
            if (subscriptionType.getDurationDays() != null) {
                team.setSubscriptionExpiry(now.plusDays(subscriptionType.getDurationDays()));
            }
        }

        Team savedTeam = teamRepository.save(team);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TeamResponse(savedTeam));
    }

    // Update team
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequest teamRequest) {
        return teamRepository.findById(id)
                .map(team -> {
                    // Check if name is being changed and if the new name already exists
                    if (!team.getName().equals(teamRequest.getName()) && 
                            teamRepository.existsByName(teamRequest.getName())) {
                        return ResponseEntity
                                .badRequest()
                                .body(String.format("Team with name '%s' already exists", teamRequest.getName()));
                    }
                    
                    // Get country
                    Country country = countryRepository.findById(teamRequest.getCountryId())
                            .orElseThrow(() -> new RuntimeException("Error: Country not found."));
                    
                    // Get subscription type if provided
                    SubscriptionType subscriptionType = null;
                    if (teamRequest.getSubscriptionTypeId() != null) {
                        subscriptionType = subscriptionTypeRepository.findById(teamRequest.getSubscriptionTypeId())
                                .orElseThrow(() -> new RuntimeException("Error: Subscription type not found."));
                        
                        // Update subscription dates if subscription type is changed
                        if (team.getSubscriptionType() == null || 
                                !team.getSubscriptionType().getId().equals(teamRequest.getSubscriptionTypeId())) {
                            LocalDateTime now = LocalDateTime.now();
                            team.setSubscriptionStart(now);
                            
                            if (subscriptionType.getDurationDays() != null) {
                                team.setSubscriptionExpiry(now.plusDays(subscriptionType.getDurationDays()));
                            } else {
                                team.setSubscriptionExpiry(null);
                            }
                        }
                    } else {
                        team.setSubscriptionStart(null);
                        team.setSubscriptionExpiry(null);
                    }
                    
                    team.setName(teamRequest.getName());
                    team.setLogoUrl(teamRequest.getLogoUrl());
                    team.setStadiumName(teamRequest.getStadiumName());
                    team.setStadiumLocation(teamRequest.getStadiumLocation());
                    team.setCountry(country);
                    team.setSubscriptionType(subscriptionType);
                    team.setIsActive(teamRequest.getIsActive());
                    team.setUpdatedAt(LocalDateTime.now());
                    
                    Team updatedTeam = teamRepository.save(team);
                    return ResponseEntity.ok(new TeamResponse(updatedTeam));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete team
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        return teamRepository.findById(id)
                .map(team -> {
                    teamRepository.delete(team);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Search teams by name
    @GetMapping("/search")
    public ResponseEntity<List<TeamResponse>> searchTeams(@RequestParam String name) {
        List<TeamResponse> teams = teamRepository.findByNameContainingIgnoreCase(name).stream()
                .map(TeamResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(teams);
    }
    
    // Get teams by country
    @GetMapping("/by-country/{countryId}")
    public ResponseEntity<List<TeamResponse>> getTeamsByCountry(@PathVariable Long countryId) {
        return countryRepository.findById(countryId)
                .map(country -> {
                    List<TeamResponse> teams = teamRepository.findByCountry(country).stream()
                            .map(TeamResponse::new)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(teams);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Get active/inactive teams
    @GetMapping("/by-status")
    public ResponseEntity<List<TeamResponse>> getTeamsByStatus(@RequestParam Boolean active) {
        List<TeamResponse> teams = teamRepository.findByIsActive(active).stream()
                .map(TeamResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(teams);
    }
    
    // Get subscription types for teams
    @GetMapping("/subscription-types")
    public ResponseEntity<List<SubscriptionTypeResponse>> getSubscriptionTypesForTeams() {
        List<SubscriptionTypeResponse> subscriptionTypes = subscriptionTypeRepository.findByIsActive(true).stream()
                .map(SubscriptionTypeResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(subscriptionTypes);
    }
}
