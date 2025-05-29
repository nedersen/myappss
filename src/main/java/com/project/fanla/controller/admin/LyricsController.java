package com.project.fanla.controller.admin;

import com.project.fanla.model.entity.Lyrics;
import com.project.fanla.model.entity.Sound;
import com.project.fanla.model.entity.Team;
import com.project.fanla.model.entity.User;
import com.project.fanla.payload.response.MessageResponse;
import com.project.fanla.payload.request.LyricsRequest;
import com.project.fanla.payload.response.LyricsResponse;
import com.project.fanla.repository.LyricsRepository;
import com.project.fanla.repository.SoundRepository;
import com.project.fanla.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for managing lyrics by admin users
 */
@RestController
@RequestMapping("/api/admin/lyrics")
@PreAuthorize("hasRole('ROLE_Admin')")
public class LyricsController {

    @Autowired
    private LyricsRepository lyricsRepository;

    @Autowired
    private SoundRepository soundRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all lyrics for a sound
     *
     * @param soundId the sound ID
     * @return list of lyrics responses
     */
    @GetMapping("/sound/{soundId}")
    public ResponseEntity<?> getLyricsBySoundId(@PathVariable Long soundId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if sound exists and belongs to the admin's team
        Optional<Sound> soundOptional = soundRepository.findById(soundId);
        if (soundOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Sound not found"));
        }

        Sound sound = soundOptional.get();
        Team userTeam = user.getTeam();

        // Verify the sound belongs to the admin's team
        if (!sound.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this sound"));
        }

        // Get lyrics for the sound
        List<Lyrics> lyricsList = lyricsRepository.findBySoundIdOrderBySecondAsc(soundId);
        List<LyricsResponse> lyricsResponses = lyricsList.stream()
                .map(LyricsResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lyricsResponses);
    }

    /**
     * Get a specific lyric by ID
     *
     * @param id the lyric ID
     * @return the lyric response
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getLyricsById(@PathVariable Long id) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if lyrics exists
        Optional<Lyrics> lyricsOptional = lyricsRepository.findById(id);
        if (lyricsOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Lyrics not found"));
        }

        Lyrics lyrics = lyricsOptional.get();
        Team userTeam = user.getTeam();

        // Verify the lyrics' sound belongs to the admin's team
        if (!lyrics.getSound().getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to these lyrics"));
        }

        return ResponseEntity.ok(LyricsResponse.fromEntity(lyrics));
    }

    /**
     * Create a new lyric for a sound
     *
     * @param soundId the sound ID
     * @param lyricsRequest the lyric request
     * @return the created lyric response
     */
    @PostMapping("/sound/{soundId}")
    public ResponseEntity<?> createLyrics(@PathVariable Long soundId, @Valid @RequestBody LyricsRequest lyricsRequest) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if sound exists and belongs to the admin's team
        Optional<Sound> soundOptional = soundRepository.findById(soundId);
        if (soundOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Sound not found"));
        }

        Sound sound = soundOptional.get();
        Team userTeam = user.getTeam();

        // Verify the sound belongs to the admin's team
        if (!sound.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this sound"));
        }

        // Create and save the new lyrics
        Lyrics lyrics = new Lyrics(
                lyricsRequest.getLyric(),
                lyricsRequest.getSecond(),
                sound
        );

        lyrics = lyricsRepository.save(lyrics);
        return ResponseEntity.status(HttpStatus.CREATED).body(LyricsResponse.fromEntity(lyrics));
    }

    /**
     * Create multiple lyrics for a sound
     *
     * @param soundId the sound ID
     * @param lyricsRequests the list of lyric requests
     * @return the list of created lyric responses
     */
    @PostMapping("/sound/{soundId}/batch")
    public ResponseEntity<?> createBatchLyrics(@PathVariable Long soundId, @Valid @RequestBody List<LyricsRequest> lyricsRequests) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if sound exists and belongs to the admin's team
        Optional<Sound> soundOptional = soundRepository.findById(soundId);
        if (soundOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Sound not found"));
        }

        Sound sound = soundOptional.get();
        Team userTeam = user.getTeam();

        // Verify the sound belongs to the admin's team
        if (!sound.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this sound"));
        }

        // Create and save the new lyrics batch
        List<Lyrics> lyricsList = new ArrayList<>();
        for (LyricsRequest request : lyricsRequests) {
            Lyrics lyrics = new Lyrics(
                    request.getLyric(),
                    request.getSecond(),
                    sound
            );
            lyricsList.add(lyrics);
        }

        lyricsList = lyricsRepository.saveAll(lyricsList);
        List<LyricsResponse> responses = lyricsList.stream()
                .map(LyricsResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * Update an existing lyric
     *
     * @param id the lyric ID
     * @param lyricsRequest the updated lyric request
     * @return the updated lyric response
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLyrics(@PathVariable Long id, @Valid @RequestBody LyricsRequest lyricsRequest) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if lyrics exists
        Optional<Lyrics> lyricsOptional = lyricsRepository.findById(id);
        if (lyricsOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Lyrics not found"));
        }

        Lyrics lyrics = lyricsOptional.get();
        Team userTeam = user.getTeam();

        // Verify the lyrics' sound belongs to the admin's team
        if (!lyrics.getSound().getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to these lyrics"));
        }

        // Update the lyrics
        lyrics.setLyric(lyricsRequest.getLyric());
        lyrics.setSecond(lyricsRequest.getSecond());

        lyrics = lyricsRepository.save(lyrics);
        return ResponseEntity.ok(LyricsResponse.fromEntity(lyrics));
    }

    /**
     * Delete a lyric
     *
     * @param id the lyric ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLyrics(@PathVariable Long id) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if lyrics exists
        Optional<Lyrics> lyricsOptional = lyricsRepository.findById(id);
        if (lyricsOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Lyrics not found"));
        }

        Lyrics lyrics = lyricsOptional.get();
        Team userTeam = user.getTeam();

        // Verify the lyrics' sound belongs to the admin's team
        if (!lyrics.getSound().getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to these lyrics"));
        }

        // Delete the lyrics
        lyricsRepository.delete(lyrics);
        return ResponseEntity.ok(new MessageResponse("Lyrics deleted successfully"));
    }

    /**
     * Delete all lyrics for a sound
     *
     * @param soundId the sound ID
     * @return success message
     */
    @DeleteMapping("/sound/{soundId}")
    public ResponseEntity<?> deleteAllLyricsForSound(@PathVariable Long soundId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getTeam() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this resource"));
        }

        // Check if sound exists and belongs to the admin's team
        Optional<Sound> soundOptional = soundRepository.findById(soundId);
        if (soundOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Sound not found"));
        }

        Sound sound = soundOptional.get();
        Team userTeam = user.getTeam();

        // Verify the sound belongs to the admin's team
        if (!sound.getTeam().getId().equals(userTeam.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have access to this sound"));
        }

        // Delete all lyrics for the sound
        lyricsRepository.deleteBySoundId(soundId);
        return ResponseEntity.ok(new MessageResponse("All lyrics for the sound deleted successfully"));
    }
}
