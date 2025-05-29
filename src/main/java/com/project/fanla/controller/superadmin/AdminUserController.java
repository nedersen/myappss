package com.project.fanla.controller.superadmin;

import com.project.fanla.model.entity.Role;
import com.project.fanla.model.entity.Team;
import com.project.fanla.model.entity.User;
import com.project.fanla.model.enums.RoleName;
import com.project.fanla.payload.request.AdminUserRequest;
import com.project.fanla.payload.response.AdminUserResponse;
import com.project.fanla.payload.response.TeamDropdownResponse;
import com.project.fanla.repository.RoleRepository;
import com.project.fanla.repository.TeamRepository;
import com.project.fanla.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/superadmin/admin-users")
@PreAuthorize("hasRole('ROLE_SuperAdmin')")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder encoder;

    // Get all admin users
    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getAllAdminUsers() {
        Role adminRole = roleRepository.findByName(RoleName.Admin)
                .orElseThrow(() -> new RuntimeException("Error: Admin role not found."));
        
        List<AdminUserResponse> adminUsers = userRepository.findByRole(adminRole).stream()
                .map(AdminUserResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(adminUsers);
    }

    // Get admin user by ID
    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> getAdminUserById(@PathVariable Long id) {
        Role adminRole = roleRepository.findByName(RoleName.Admin)
                .orElseThrow(() -> new RuntimeException("Error: Admin role not found."));
        
        return userRepository.findByIdAndRole(id, adminRole)
                .map(AdminUserResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new admin user
    @PostMapping
    public ResponseEntity<?> createAdminUser(@Valid @RequestBody AdminUserRequest adminUserRequest) {
        // Check if username is already taken
        if (userRepository.existsByUsername(adminUserRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(String.format("Username '%s' is already taken", adminUserRequest.getUsername()));
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(adminUserRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(String.format("Email '%s' is already in use", adminUserRequest.getEmail()));
        }

        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Get admin role
        Role adminRole = roleRepository.findByName(RoleName.Admin)
                .orElseThrow(() -> new RuntimeException("Error: Admin role not found."));
        
        // Get team
        Team team = teamRepository.findById(adminUserRequest.getTeamId())
                .orElseThrow(() -> new RuntimeException("Error: Team not found."));

        // Create new admin user
        User adminUser = new User();
        adminUser.setUsername(adminUserRequest.getUsername());
        adminUser.setEmail(adminUserRequest.getEmail());
        adminUser.setPassword(encoder.encode(adminUserRequest.getPassword()));
        adminUser.setRole(adminRole);
        adminUser.setTeam(team);
        adminUser.setIsActive(adminUserRequest.getIsActive());
        adminUser.setCreatedBy(currentUser);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(adminUser);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AdminUserResponse(savedUser));
    }

    // Update admin user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdminUser(@PathVariable Long id, @Valid @RequestBody AdminUserRequest adminUserRequest) {
        Role adminRole = roleRepository.findByName(RoleName.Admin)
                .orElseThrow(() -> new RuntimeException("Error: Admin role not found."));
        
        return userRepository.findByIdAndRole(id, adminRole)
                .map(adminUser -> {
                    // Check if username is being changed and if the new username already exists
                    if (!adminUser.getUsername().equals(adminUserRequest.getUsername()) && 
                            userRepository.existsByUsername(adminUserRequest.getUsername())) {
                        return ResponseEntity
                                .badRequest()
                                .body(String.format("Username '%s' is already taken", adminUserRequest.getUsername()));
                    }
                    
                    // Check if email is being changed and if the new email already exists
                    if (!adminUser.getEmail().equals(adminUserRequest.getEmail()) && 
                            userRepository.existsByEmail(adminUserRequest.getEmail())) {
                        return ResponseEntity
                                .badRequest()
                                .body(String.format("Email '%s' is already in use", adminUserRequest.getEmail()));
                    }
                    
                    // Get team
                    Team team = teamRepository.findById(adminUserRequest.getTeamId())
                            .orElseThrow(() -> new RuntimeException("Error: Team not found."));
                    
                    adminUser.setUsername(adminUserRequest.getUsername());
                    adminUser.setEmail(adminUserRequest.getEmail());
                    
                    // Only update password if provided
                    if (adminUserRequest.getPassword() != null && !adminUserRequest.getPassword().isEmpty()) {
                        adminUser.setPassword(encoder.encode(adminUserRequest.getPassword()));
                    }
                    
                    adminUser.setTeam(team);
                    adminUser.setIsActive(adminUserRequest.getIsActive());
                    adminUser.setUpdatedAt(LocalDateTime.now());
                    
                    User updatedUser = userRepository.save(adminUser);
                    return ResponseEntity.ok(new AdminUserResponse(updatedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete admin user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdminUser(@PathVariable Long id) {
        Role adminRole = roleRepository.findByName(RoleName.Admin)
                .orElseThrow(() -> new RuntimeException("Error: Admin role not found."));
        
        return userRepository.findByIdAndRole(id, adminRole)
                .map(adminUser -> {
                    userRepository.delete(adminUser);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Search admin users by username
    @GetMapping("/search")
    public ResponseEntity<List<AdminUserResponse>> searchAdminUsers(@RequestParam String username) {
        Role adminRole = roleRepository.findByName(RoleName.Admin)
                .orElseThrow(() -> new RuntimeException("Error: Admin role not found."));
        
        List<AdminUserResponse> adminUsers = userRepository.findByUsernameContainingIgnoreCaseAndRole(username, adminRole).stream()
                .map(AdminUserResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(adminUsers);
    }
    
    // Get admin users by team
    @GetMapping("/by-team/{teamId}")
    public ResponseEntity<List<AdminUserResponse>> getAdminUsersByTeam(@PathVariable Long teamId) {
        return teamRepository.findById(teamId)
                .map(team -> {
                    Role adminRole = roleRepository.findByName(RoleName.Admin)
                            .orElseThrow(() -> new RuntimeException("Error: Admin role not found."));
                    
                    List<AdminUserResponse> adminUsers = userRepository.findByTeamAndRole(team, adminRole).stream()
                            .map(AdminUserResponse::new)
                            .collect(Collectors.toList());
                    
                    return ResponseEntity.ok(adminUsers);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Get active/inactive admin users
    @GetMapping("/by-status")
    public ResponseEntity<List<AdminUserResponse>> getAdminUsersByStatus(@RequestParam Boolean active) {
        Role adminRole = roleRepository.findByName(RoleName.Admin)
                .orElseThrow(() -> new RuntimeException("Error: Admin role not found."));
        
        List<AdminUserResponse> adminUsers = userRepository.findByIsActiveAndRole(active, adminRole).stream()
                .map(AdminUserResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(adminUsers);
    }
    
    // Get teams for dropdown
    @GetMapping("/teams-dropdown")
    public ResponseEntity<List<TeamDropdownResponse>> getTeamsForDropdown() {
        List<TeamDropdownResponse> teams = teamRepository.findByIsActive(true).stream()
                .map(TeamDropdownResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(teams);
    }
}
