package com.project.fanla.controller.admin;

import com.project.fanla.model.entity.MatchSoundDetail;
import com.project.fanla.model.entity.Sound;
import com.project.fanla.model.entity.Team;
import com.project.fanla.model.entity.User;
import com.project.fanla.model.enums.SoundStatus;
import com.project.fanla.payload.request.SoundRequest;
import com.project.fanla.payload.response.SoundResponse;
import com.project.fanla.repository.MatchSoundDetailRepository;
import com.project.fanla.repository.SoundRepository;
import com.project.fanla.repository.TeamRepository;
import com.project.fanla.repository.UserRepository;
import com.project.fanla.service.InternetArchiveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/sounds")
@PreAuthorize("hasRole('ROLE_Admin')")
public class SoundController {

    @Autowired
    private SoundRepository soundRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InternetArchiveService internetArchiveService;
    
    @Autowired
    private MatchSoundDetailRepository matchSoundDetailRepository;

    // Get all sounds for admin's team
    @GetMapping
    public ResponseEntity<?> getAllSounds() {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Get admin's team
        Team team = currentUser.getTeam();
        if (team == null) {
            return ResponseEntity.badRequest().body("Error: Admin is not assigned to any team.");
        }
        
        List<SoundResponse> sounds = soundRepository.findByTeam(team).stream()
                .map(SoundResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(sounds);
    }

    // Get sound by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSoundById(@PathVariable Long id) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Get admin's team
        Team team = currentUser.getTeam();
        if (team == null) {
            return ResponseEntity.badRequest().body("Error: Admin is not assigned to any team.");
        }
        
        return soundRepository.findById(id)
                .map(sound -> {
                    // Check if sound belongs to admin's team
                    if (!sound.getTeam().getId().equals(team.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Error: You don't have permission to access this sound.");
                    }
                    return ResponseEntity.ok(new SoundResponse(sound));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Upload sound file to Internet Archive and create a new sound
    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndCreateSound(
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "status", required = false) SoundStatus status) {
        
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Get admin's team
        Team team = currentUser.getTeam();
        if (team == null) {
            return ResponseEntity.badRequest().body("Error: Admin is not assigned to any team.");
        }
        
        try {
            // Upload sound file to Internet Archive
            String soundUrl = internetArchiveService.uploadFile(file);
            
            // Upload image file to Internet Archive if provided
            String imageUrl = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = internetArchiveService.uploadFile(imageFile);
            }
            
            // Create new sound with the uploaded file URLs
            Sound sound = new Sound();
            sound.setTitle(title);
            sound.setSoundUrl(soundUrl);
            sound.setSoundImageUrl(imageUrl);
            sound.setTeam(team);
            sound.setStatus(status != null ? status : SoundStatus.STOPPED);
            sound.setCurrentMillisecond(0L);
            
            Sound savedSound = soundRepository.save(sound);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SoundResponse(savedSound));
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }

    // Create a new sound with existing URLs
    @PostMapping
    public ResponseEntity<?> createSound(@Valid @RequestBody SoundRequest soundRequest) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Get admin's team
        Team team = currentUser.getTeam();
        if (team == null) {
            return ResponseEntity.badRequest().body("Error: Admin is not assigned to any team.");
        }

        // Create new sound
        Sound sound = new Sound();
        sound.setTitle(soundRequest.getTitle());
        sound.setSoundUrl(soundRequest.getSoundUrl());
        sound.setSoundImageUrl(soundRequest.getSoundImageUrl());
        sound.setTeam(team);
        sound.setStatus(soundRequest.getStatus() != null ? soundRequest.getStatus() : SoundStatus.STOPPED);
        sound.setCurrentMillisecond(soundRequest.getCurrentMillisecond() != null ? soundRequest.getCurrentMillisecond() : 0L);

        Sound savedSound = soundRepository.save(sound);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SoundResponse(savedSound));
    }

    // Upload a sound image and update existing sound
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadSoundImage(
            @PathVariable Long id,
            @RequestParam("imageFile") MultipartFile imageFile) {
        
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Get admin's team
        Team team = currentUser.getTeam();
        if (team == null) {
            return ResponseEntity.badRequest().body("Error: Admin is not assigned to any team.");
        }
        
        return soundRepository.findById(id)
                .map(sound -> {
                    // Check if sound belongs to admin's team
                    if (!sound.getTeam().getId().equals(team.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Error: You don't have permission to update this sound.");
                    }
                    
                    try {
                        // Upload image file to Internet Archive
                        String imageUrl = internetArchiveService.uploadFile(imageFile);
                        
                        // Update sound with the image URL
                        sound.setSoundImageUrl(imageUrl);
                        Sound updatedSound = soundRepository.save(sound);
                        
                        return ResponseEntity.ok(new SoundResponse(updatedSound));
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error uploading image file: " + e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Update sound
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSound(@PathVariable Long id, @Valid @RequestBody SoundRequest soundRequest) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Get admin's team
        Team team = currentUser.getTeam();
        if (team == null) {
            return ResponseEntity.badRequest().body("Error: Admin is not assigned to any team.");
        }
        
        return soundRepository.findById(id)
                .map(sound -> {
                    // Check if sound belongs to admin's team
                    if (!sound.getTeam().getId().equals(team.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Error: You don't have permission to update this sound.");
                    }
                    
                    sound.setTitle(soundRequest.getTitle());
                    if (soundRequest.getSoundUrl() != null) {
                        sound.setSoundUrl(soundRequest.getSoundUrl());
                    }
                    if (soundRequest.getSoundImageUrl() != null) {
                        sound.setSoundImageUrl(soundRequest.getSoundImageUrl());
                    }
                    sound.setStatus(soundRequest.getStatus());
                    sound.setCurrentMillisecond(soundRequest.getCurrentMillisecond());
                    
                    Sound updatedSound = soundRepository.save(sound);
                    return ResponseEntity.ok(new SoundResponse(updatedSound));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete sound
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSound(@PathVariable Long id) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Get admin's team
        Team team = currentUser.getTeam();
        if (team == null) {
            return ResponseEntity.badRequest().body("Error: Admin is not assigned to any team.");
        }
        
        return soundRepository.findById(id)
                .map(sound -> {
                    // Check if sound belongs to admin's team
                    if (!sound.getTeam().getId().equals(team.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Error: You don't have permission to delete this sound.");
                    }
                    
                    try {
                        // First, find all MatchSoundDetail entities that reference this sound
                        List<MatchSoundDetail> matchSoundDetails = matchSoundDetailRepository.findByActiveSound(sound);
                        
                        // Remove the references by setting activeSound to null
                        for (MatchSoundDetail detail : matchSoundDetails) {
                            detail.setActiveSound(null);
                            matchSoundDetailRepository.save(detail);
                        }
                        
                        // Now it's safe to delete the sound
                        soundRepository.delete(sound);
                        return ResponseEntity.ok().body("Sound deleted successfully");
                    } catch (DataIntegrityViolationException e) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body("Error: Cannot delete sound because it is referenced by other entities. Please contact support.");
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error deleting sound: " + e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Search sounds by title
    @GetMapping("/search")
    public ResponseEntity<?> searchSounds(@RequestParam String title) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Get admin's team
        Team team = currentUser.getTeam();
        if (team == null) {
            return ResponseEntity.badRequest().body("Error: Admin is not assigned to any team.");
        }
        
        List<SoundResponse> sounds = soundRepository.findByTeamAndTitleContainingIgnoreCase(team, title).stream()
                .map(SoundResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(sounds);
    }
    
    // Update sound status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateSoundStatus(@PathVariable Long id, @RequestParam SoundStatus status) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Get admin's team
        Team team = currentUser.getTeam();
        if (team == null) {
            return ResponseEntity.badRequest().body("Error: Admin is not assigned to any team.");
        }
        
        return soundRepository.findById(id)
                .map(sound -> {
                    // Check if sound belongs to admin's team
                    if (!sound.getTeam().getId().equals(team.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Error: You don't have permission to update this sound.");
                    }
                    
                    sound.setStatus(status);
                    Sound updatedSound = soundRepository.save(sound);
                    return ResponseEntity.ok(new SoundResponse(updatedSound));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Update sound current millisecond
    @PutMapping("/{id}/position")
    public ResponseEntity<?> updateSoundPosition(@PathVariable Long id, @RequestParam Long millisecond) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Get admin's team
        Team team = currentUser.getTeam();
        if (team == null) {
            return ResponseEntity.badRequest().body("Error: Admin is not assigned to any team.");
        }
        
        return soundRepository.findById(id)
                .map(sound -> {
                    // Check if sound belongs to admin's team
                    if (!sound.getTeam().getId().equals(team.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Error: You don't have permission to update this sound.");
                    }
                    
                    sound.setCurrentMillisecond(millisecond);
                    Sound updatedSound = soundRepository.save(sound);
                    return ResponseEntity.ok(new SoundResponse(updatedSound));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
