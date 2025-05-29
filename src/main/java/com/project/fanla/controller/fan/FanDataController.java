package com.project.fanla.controller.fan;

import com.project.fanla.model.entity.Country;
import com.project.fanla.model.entity.Match;
import com.project.fanla.model.entity.Sound;
import com.project.fanla.model.entity.Team;
import com.project.fanla.payload.request.SoundDownloadStatusRequest;
import com.project.fanla.payload.response.CountryResponse;
import com.project.fanla.payload.response.FanSoundResponse;
import com.project.fanla.payload.response.FanTeamResponse;
import com.project.fanla.payload.response.MatchResponse;
import com.project.fanla.repository.CountryRepository;
import com.project.fanla.repository.MatchRepository;
import com.project.fanla.repository.SoundRepository;
import com.project.fanla.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fan")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FanDataController {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private MatchRepository matchRepository;
    
    @Autowired
    private SoundRepository soundRepository;

    /**
     * Get all countries
     * 
     * @return List of all countries
     */
    @GetMapping("/countries")
    public ResponseEntity<?> getAllCountries() {
        List<Country> countries = countryRepository.findAll();
        List<CountryResponse> countryResponses = countries.stream()
                .map(CountryResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(countryResponses);
    }

    /**
     * Get a specific country by ID
     * 
     * @param id Country ID
     * @return Country details
     */
    @GetMapping("/countries/{id}")
    public ResponseEntity<?> getCountryById(@PathVariable Long id) {
        return countryRepository.findById(id)
                .map(country -> ResponseEntity.ok(new CountryResponse(country)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all teams in a specific country
     * 
     * @param countryId Country ID
     * @return List of teams in the country
     */
    @GetMapping("/countries/{countryId}/teams")
    public ResponseEntity<?> getTeamsByCountry(@PathVariable Long countryId) {
        return countryRepository.findById(countryId)
                .map(country -> {
                    List<Team> teams = teamRepository.findByCountry(country);
                    List<FanTeamResponse> teamResponses = teams.stream()
                            .filter(Team::getIsActive)  // Only return active teams
                            .map(FanTeamResponse::new)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(teamResponses);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all active teams across all countries
     * 
     * @return List of all active teams
     */
    @GetMapping("/teams")
    public ResponseEntity<?> getAllTeams() {
        List<Team> teams = teamRepository.findByIsActive(true);
        List<FanTeamResponse> teamResponses = teams.stream()
                .map(FanTeamResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(teamResponses);
    }

    /**
     * Get a specific team by ID
     * 
     * @param id Team ID
     * @return Team details
     */
    @GetMapping("/teams/{id}")
    public ResponseEntity<?> getTeamById(@PathVariable Long id) {
        return teamRepository.findById(id)
                .filter(Team::getIsActive)  // Only return if team is active
                .map(team -> ResponseEntity.ok(new FanTeamResponse(team)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all matches for a specific team
     * 
     * @param teamId Team ID
     * @return List of matches for the team
     */
    @GetMapping("/teams/{teamId}/matches")
    public ResponseEntity<?> getMatchesByTeamId(@PathVariable Long teamId) {
        return teamRepository.findById(teamId)
                .filter(Team::getIsActive)  // Only return if team is active
                .map(team -> {
                    List<Match> matches = matchRepository.findByTeamOrderByMatchDateDesc(team);
                    List<MatchResponse> matchResponses = matches.stream()
                            .map(MatchResponse::fromEntity)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(matchResponses);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get upcoming matches for a specific team
     * 
     * @param teamId Team ID
     * @return List of upcoming matches for the team
     */
    @GetMapping("/teams/{teamId}/matches/upcoming")
    public ResponseEntity<?> getUpcomingMatchesByTeamId(@PathVariable Long teamId) {
        return teamRepository.findById(teamId)
                .filter(Team::getIsActive)  // Only return if team is active
                .map(team -> {
                    List<Match> matches = matchRepository.findByTeamAndMatchDateAfter(team, java.time.LocalDateTime.now());
                    List<MatchResponse> matchResponses = matches.stream()
                            .map(MatchResponse::fromEntity)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(matchResponses);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get past matches for a specific team
     * 
     * @param teamId Team ID
     * @return List of past matches for the team
     */
    @GetMapping("/teams/{teamId}/matches/past")
    public ResponseEntity<?> getPastMatchesByTeamId(@PathVariable Long teamId) {
        return teamRepository.findById(teamId)
                .filter(Team::getIsActive)  // Only return if team is active
                .map(team -> {
                    List<Match> matches = matchRepository.findByTeamAndMatchDateBefore(team, java.time.LocalDateTime.now());
                    List<MatchResponse> matchResponses = matches.stream()
                            .map(MatchResponse::fromEntity)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(matchResponses);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all sounds for a specific team
     * 
     * @param teamId Team ID
     * @return List of sounds for the team
     */
    @GetMapping("/teams/{teamId}/sounds")
    public ResponseEntity<?> getSoundsByTeamId(@PathVariable Long teamId) {
        return teamRepository.findById(teamId)
                .filter(Team::getIsActive)  // Only return if team is active
                .map(team -> {
                    List<Sound> sounds = soundRepository.findByTeam(team);
                    List<FanSoundResponse> soundResponses = sounds.stream()
                            .map(FanSoundResponse::new)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(soundResponses);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Check download status for a list of sounds
     * Client sends a list of sound IDs and their download status
     * Server returns the complete list of sounds with updated download status
     * 
     * @param teamId Team ID
     * @param request Request containing sound IDs and their download status
     * @return List of sounds with updated download status
     */
    @PostMapping("/teams/{teamId}/sounds/check-download-status")
    public ResponseEntity<?> checkSoundDownloadStatus(
            @PathVariable Long teamId,
            @RequestBody SoundDownloadStatusRequest request) {
        
        return teamRepository.findById(teamId)
                .filter(Team::getIsActive)  // Only return if team is active
                .map(team -> {
                    // Get all sounds for the team
                    List<Sound> teamSounds = soundRepository.findByTeam(team);
                    
                    // Create a map of download status from the request
                    Map<Long, Boolean> downloadStatusMap = new HashMap<>();
                    if (request.getDownloadStatus() != null) {
                        downloadStatusMap.putAll(request.getDownloadStatus());
                    }
                    
                    // Create response with download status
                    List<FanSoundResponse> soundResponses = teamSounds.stream()
                            .map(sound -> {
                                FanSoundResponse response = new FanSoundResponse(sound);
                                // Set download status if available in the request
                                if (downloadStatusMap.containsKey(sound.getId())) {
                                    response.setIsDownloaded(downloadStatusMap.get(sound.getId()));
                                }
                                return response;
                            })
                            .collect(Collectors.toList());
                    
                    return ResponseEntity.ok(soundResponses);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get a specific sound by ID
     * 
     * @param teamId Team ID
     * @param soundId Sound ID
     * @return Sound details
     */
    @GetMapping("/teams/{teamId}/sounds/{soundId}")
    public ResponseEntity<?> getSoundById(
            @PathVariable Long teamId,
            @PathVariable Long soundId) {
        
        return teamRepository.findById(teamId)
                .filter(Team::getIsActive)  // Only return if team is active
                .map(team -> 
                    soundRepository.findById(soundId)
                        .filter(sound -> sound.getTeam().getId().equals(team.getId()))
                        .map(sound -> ResponseEntity.ok(new FanSoundResponse(sound)))
                        .orElse(ResponseEntity.notFound().build())
                )
                .orElse(ResponseEntity.notFound().build());
    }
}
