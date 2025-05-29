package com.project.fanla.controller.superadmin;

import com.project.fanla.model.entity.SubscriptionType;
import com.project.fanla.model.entity.User;
import com.project.fanla.payload.request.SubscriptionTypeRequest;
import com.project.fanla.payload.response.SubscriptionTypeResponse;
import com.project.fanla.repository.SubscriptionTypeRepository;
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
@RequestMapping("/api/superadmin/subscription-types")
@PreAuthorize("hasRole('ROLE_SuperAdmin')")
public class SubscriptionTypeController {

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;
    
    @Autowired
    private UserRepository userRepository;

    // Get all subscription types
    @GetMapping
    public ResponseEntity<List<SubscriptionTypeResponse>> getAllSubscriptionTypes() {
        List<SubscriptionTypeResponse> subscriptionTypes = subscriptionTypeRepository.findAll().stream()
                .map(SubscriptionTypeResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(subscriptionTypes);
    }

    // Get subscription type by ID
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionTypeResponse> getSubscriptionTypeById(@PathVariable Long id) {
        return subscriptionTypeRepository.findById(id)
                .map(SubscriptionTypeResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new subscription type
    @PostMapping
    public ResponseEntity<?> createSubscriptionType(@Valid @RequestBody SubscriptionTypeRequest subscriptionTypeRequest) {
        // Check if subscription type with the same name already exists
        if (subscriptionTypeRepository.existsByName(subscriptionTypeRequest.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body(String.format("Subscription type with name '%s' already exists", subscriptionTypeRequest.getName()));
        }

        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Create new subscription type
        SubscriptionType subscriptionType = new SubscriptionType();
        subscriptionType.setName(subscriptionTypeRequest.getName());
        subscriptionType.setMaxClients(subscriptionTypeRequest.getMaxClients());
        subscriptionType.setMaxMatches(subscriptionTypeRequest.getMaxMatches());
        subscriptionType.setPrice(subscriptionTypeRequest.getPrice());
        subscriptionType.setDurationDays(subscriptionTypeRequest.getDurationDays());
        subscriptionType.setDescription(subscriptionTypeRequest.getDescription());
        subscriptionType.setIsActive(subscriptionTypeRequest.getIsActive());
        subscriptionType.setCreatedBy(currentUser);
        subscriptionType.setCreatedAt(LocalDateTime.now());
        subscriptionType.setUpdatedAt(LocalDateTime.now());

        SubscriptionType savedSubscriptionType = subscriptionTypeRepository.save(subscriptionType);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SubscriptionTypeResponse(savedSubscriptionType));
    }

    // Update subscription type
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubscriptionType(@PathVariable Long id, @Valid @RequestBody SubscriptionTypeRequest subscriptionTypeRequest) {
        return subscriptionTypeRepository.findById(id)
                .map(subscriptionType -> {
                    // Check if name is being changed and if the new name already exists
                    if (!subscriptionType.getName().equals(subscriptionTypeRequest.getName()) && 
                            subscriptionTypeRepository.existsByName(subscriptionTypeRequest.getName())) {
                        return ResponseEntity
                                .badRequest()
                                .body(String.format("Subscription type with name '%s' already exists", subscriptionTypeRequest.getName()));
                    }
                    
                    subscriptionType.setName(subscriptionTypeRequest.getName());
                    subscriptionType.setMaxClients(subscriptionTypeRequest.getMaxClients());
                    subscriptionType.setMaxMatches(subscriptionTypeRequest.getMaxMatches());
                    subscriptionType.setPrice(subscriptionTypeRequest.getPrice());
                    subscriptionType.setDurationDays(subscriptionTypeRequest.getDurationDays());
                    subscriptionType.setDescription(subscriptionTypeRequest.getDescription());
                    subscriptionType.setIsActive(subscriptionTypeRequest.getIsActive());
                    subscriptionType.setUpdatedAt(LocalDateTime.now());
                    
                    SubscriptionType updatedSubscriptionType = subscriptionTypeRepository.save(subscriptionType);
                    return ResponseEntity.ok(new SubscriptionTypeResponse(updatedSubscriptionType));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete subscription type
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubscriptionType(@PathVariable Long id) {
        return subscriptionTypeRepository.findById(id)
                .map(subscriptionType -> {
                    // Check if subscription type is being used by any teams
                    if (!subscriptionType.getTeams().isEmpty()) {
                        return ResponseEntity
                                .badRequest()
                                .body(String.format("Cannot delete subscription type '%s' as it is being used by %d teams", 
                                        subscriptionType.getName(), subscriptionType.getTeams().size()));
                    }
                    
                    subscriptionTypeRepository.delete(subscriptionType);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Search subscription types by name
    @GetMapping("/search")
    public ResponseEntity<List<SubscriptionTypeResponse>> searchSubscriptionTypes(@RequestParam String name) {
        List<SubscriptionTypeResponse> subscriptionTypes = subscriptionTypeRepository.findByNameContainingIgnoreCase(name).stream()
                .map(SubscriptionTypeResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(subscriptionTypes);
    }
    
    // Get active/inactive subscription types
    @GetMapping("/by-status")
    public ResponseEntity<List<SubscriptionTypeResponse>> getSubscriptionTypesByStatus(@RequestParam Boolean active) {
        List<SubscriptionTypeResponse> subscriptionTypes = subscriptionTypeRepository.findByIsActive(active).stream()
                .map(SubscriptionTypeResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(subscriptionTypes);
    }
    
    // Get subscription types for dropdown (simplified version for UI)
    @GetMapping("/dropdown")
    public ResponseEntity<List<SubscriptionTypeResponse>> getSubscriptionTypesForDropdown() {
        List<SubscriptionTypeResponse> subscriptionTypes = subscriptionTypeRepository.findByIsActive(true).stream()
                .map(SubscriptionTypeResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(subscriptionTypes);
    }
}
