package com.project.fanla.controller.admin;

import com.project.fanla.model.entity.Match;
import com.project.fanla.model.entity.MatchSoundDetail;
import com.project.fanla.model.entity.Team;
import com.project.fanla.model.entity.User;
import com.project.fanla.model.enums.EventType;
import com.project.fanla.model.enums.MatchStatus;
import com.project.fanla.model.enums.SoundStatus;
import com.project.fanla.payload.request.MatchRequest;
import com.project.fanla.payload.response.MatchResponse;
import com.project.fanla.payload.response.MessageResponse;
import com.project.fanla.repository.MatchRepository;
import com.project.fanla.repository.MatchSoundDetailRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for managing matches by admin users
 */
@RestController
@RequestMapping("/api/admin/matches")
@PreAuthorize("hasRole('ROLE_Admin')")
public class MatchController {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MatchSoundDetailRepository matchSoundDetailRepository;

    /**
     * Get all matches for the admin's team
     *
     * @return list of match responses
     */
    @GetMapping
    public ResponseEntity<?> getAllMatches() {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        Team team = user.getTeam();

        // Get matches for the team
        List<Match> matches = matchRepository.findByTeamOrderByMatchDateDesc(team);
        List<MatchResponse> matchResponses = matches.stream()
                .map(MatchResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(matchResponses);
    }

    /**
     * Get a specific match by ID
     *
     * @param id the match ID
     * @return the match response
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMatchById(@PathVariable Long id) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match not found"));
        }

        Match match = matchOptional.get();
        Team userTeam = user.getTeam();

        // Verify the match belongs to the admin's team
        if (!match.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match"));
        }

        return ResponseEntity.ok(MatchResponse.fromEntity(match));
    }

    /**
     * Create a new match
     *
     * @param matchRequest the match request
     * @return the created match response
     */
    @PostMapping
    public ResponseEntity<?> createMatch(@Valid @RequestBody MatchRequest matchRequest) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        Team team = user.getTeam();
        
        // Validate opponent information
        if (!matchRequest.hasValidOpponent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Either opponentTeamId or manualOpponentName must be provided"));
        }

        // Get opponent team if provided
        Team opponentTeam = null;
        if (matchRequest.getOpponentTeamId() != null) {
            Optional<Team> opponentTeamOptional = teamRepository.findById(matchRequest.getOpponentTeamId());
            if (opponentTeamOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Opponent team not found"));
            }
            opponentTeam = opponentTeamOptional.get();
            
            // Ensure opponent team is not the same as the admin's team
            if (opponentTeam.getId().equals(team.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("Opponent team cannot be the same as your team"));
            }
        }

        // Create and save the new match
        Match match = new Match(
                team,
                opponentTeam,
                matchRequest.getStatus(),
                matchRequest.getManualOpponentName(),
                matchRequest.getManualOpponentLogo(),
                matchRequest.getHomeScore(),
                matchRequest.getAwayScore(),
                matchRequest.getMatchDate()
        );

        match = matchRepository.save(match);
        
        // Create initial match sound detail
        MatchSoundDetail initialDetail = new MatchSoundDetail();
        initialDetail.setMatch(match);
        initialDetail.setEventType(com.project.fanla.model.enums.EventType.START);
        initialDetail.setCurrentMillisecond(0L);
        initialDetail.setSoundStatus(SoundStatus.STOPPED);
        matchSoundDetailRepository.save(initialDetail);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(MatchResponse.fromEntity(match));
    }

    /**
     * Update an existing match
     *
     * @param id the match ID
     * @param matchRequest the updated match request
     * @return the updated match response
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatch(@PathVariable Long id, @Valid @RequestBody MatchRequest matchRequest) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match not found"));
        }

        Match match = matchOptional.get();
        Team userTeam = user.getTeam();

        // Verify the match belongs to the admin's team
        if (!match.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match"));
        }
        
        // Validate opponent information
        if (!matchRequest.hasValidOpponent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Either opponentTeamId or manualOpponentName must be provided"));
        }

        // Get opponent team if provided
        Team opponentTeam = null;
        if (matchRequest.getOpponentTeamId() != null) {
            Optional<Team> opponentTeamOptional = teamRepository.findById(matchRequest.getOpponentTeamId());
            if (opponentTeamOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Opponent team not found"));
            }
            opponentTeam = opponentTeamOptional.get();
            
            // Ensure opponent team is not the same as the admin's team
            if (opponentTeam.getId().equals(userTeam.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("Opponent team cannot be the same as your team"));
            }
        }

        // Update the match
        match.setOpponentTeam(opponentTeam);
        match.setManualOpponentName(matchRequest.getManualOpponentName());
        match.setManualOpponentLogo(matchRequest.getManualOpponentLogo());
        match.setStatus(matchRequest.getStatus());
        match.setHomeScore(matchRequest.getHomeScore());
        match.setAwayScore(matchRequest.getAwayScore());
        match.setMatchDate(matchRequest.getMatchDate());

        match = matchRepository.save(match);
        return ResponseEntity.ok(MatchResponse.fromEntity(match));
    }

    /**
     * Delete a match
     *
     * @param id the match ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable Long id) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match not found"));
        }

        Match match = matchOptional.get();
        Team userTeam = user.getTeam();

        // Verify the match belongs to the admin's team
        if (!match.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match"));
        }

        // Delete the match
        matchRepository.delete(match);
        return ResponseEntity.ok(new MessageResponse("Match deleted successfully"));
    }

    /**
     * Search matches by opponent name
     *
     * @param opponentName the opponent name
     * @return list of match responses
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchMatches(@RequestParam String opponentName) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        Team team = user.getTeam();

        // Search matches by opponent name
        List<Match> matches = matchRepository.findByTeamAndManualOpponentNameContainingIgnoreCase(team, opponentName);
        List<MatchResponse> matchResponses = matches.stream()
                .map(MatchResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(matchResponses);
    }

    /**
     * Get matches by status
     *
     * @param status the match status
     * @return list of match responses
     */
    @GetMapping("/by-status")
    public ResponseEntity<?> getMatchesByStatus(@RequestParam MatchStatus status) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        Team team = user.getTeam();

        // Get matches by status
        List<Match> matches = matchRepository.findByTeamAndStatus(team, status);
        List<MatchResponse> matchResponses = matches.stream()
                .map(MatchResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(matchResponses);
    }

    /**
     * Get upcoming matches
     *
     * @return list of match responses
     */
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingMatches() {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        Team team = user.getTeam();

        // Get upcoming matches (matches after current date)
        List<Match> matches = matchRepository.findByTeamAndMatchDateAfter(team, LocalDateTime.now());
        List<MatchResponse> matchResponses = matches.stream()
                .map(MatchResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(matchResponses);
    }

    /**
     * Get past matches
     *
     * @return list of match responses
     */
    @GetMapping("/past")
    public ResponseEntity<?> getPastMatches() {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        Team team = user.getTeam();

        // Get past matches (matches before current date)
        List<Match> matches = matchRepository.findByTeamAndMatchDateBefore(team, LocalDateTime.now());
        List<MatchResponse> matchResponses = matches.stream()
                .map(MatchResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(matchResponses);
    }
    
    /**
     * Get all available teams for opponent selection (excluding admin's team)
     *
     * @return list of teams
     */
    @GetMapping("/available-opponents")
    public ResponseEntity<?> getAvailableOpponents() {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        Team userTeam = user.getTeam();

        // Get all teams except the admin's team
        List<Team> teams = teamRepository.findAll().stream()
                .filter(team -> !team.getId().equals(userTeam.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(teams);
    }
    
    /**
     * Get all available match statuses
     *
     * @return list of match statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<?> getMatchStatuses() {
        List<MatchStatus> statuses = Arrays.asList(MatchStatus.values());
        return ResponseEntity.ok(statuses);
    }
    
    /**
     * Update match status
     *
     * @param id the match ID
     * @param status the new status
     * @return the updated match response
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateMatchStatus(@PathVariable Long id, @RequestParam MatchStatus status) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match not found"));
        }

        Match match = matchOptional.get();
        Team userTeam = user.getTeam();

        // Verify the match belongs to the admin's team
        if (!match.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match"));
        }

        // Update the match status
        match.setStatus(status);
        match = matchRepository.save(match);
        
        return ResponseEntity.ok(MatchResponse.fromEntity(match));
    }
    
    /**
     * Update match score
     *
     * @param id the match ID
     * @param homeScore the home score
     * @param awayScore the away score
     * @return the updated match response
     */
    @PutMapping("/{id}/score")
    public ResponseEntity<?> updateMatchScore(
            @PathVariable Long id, 
            @RequestParam Integer homeScore, 
            @RequestParam Integer awayScore) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if match exists
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Match not found"));
        }

        Match match = matchOptional.get();
        Team userTeam = user.getTeam();

        // Verify the match belongs to the admin's team
        if (!match.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this match"));
        }

        // Update the match score
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        match = matchRepository.save(match);
        
        return ResponseEntity.ok(MatchResponse.fromEntity(match));
    }
}
