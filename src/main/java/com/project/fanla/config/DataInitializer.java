package com.project.fanla.config;

import com.project.fanla.model.entity.Role;
import com.project.fanla.model.entity.User;
import com.project.fanla.model.enums.RoleName;
import com.project.fanla.repository.RoleRepository;
import com.project.fanla.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        initRoles();
        
        // Create SuperAdmin user if it doesn't exist
        createSuperAdminIfNotExists();
    }

    private void initRoles() {
        // Check if roles already exist
        if (roleRepository.count() == 0) {
            // Create roles
            Arrays.stream(RoleName.values()).forEach(roleName -> {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                System.out.println("Created role: " + roleName);
            });
        }
    }

    private void createSuperAdminIfNotExists() {
        // Check if SuperAdmin user already exists
        if (!userRepository.existsByUsername("superadmin")) {
            // Get SuperAdmin role
            Role superAdminRole = roleRepository.findByName(RoleName.SuperAdmin)
                    .orElseThrow(() -> new RuntimeException("Error: Role SuperAdmin is not found."));

            // Create SuperAdmin user
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setEmail("superadmin@fanla.com");
            superAdmin.setPassword(passwordEncoder.encode("superadmin"));
            superAdmin.setRole(superAdminRole);
            superAdmin.setCreatedAt(LocalDateTime.now());

            userRepository.save(superAdmin);
            System.out.println("Created SuperAdmin user");
        }
    }
}
