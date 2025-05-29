package com.project.fanla.controller;

import com.project.fanla.model.entity.Role;
import com.project.fanla.model.entity.User;
import com.project.fanla.model.enums.RoleName;
import com.project.fanla.payload.JwtResponse;
import com.project.fanla.payload.LoginRequest;
import com.project.fanla.payload.MessageResponse;
import com.project.fanla.payload.TokenRefreshRequest;
import com.project.fanla.payload.TokenRefreshResponse;
import com.project.fanla.repository.RoleRepository;
import com.project.fanla.repository.UserRepository;
import com.project.fanla.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());
        
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        
        return ResponseEntity.ok(new JwtResponse(
                jwt,
                refreshToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getName().name()));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        if (!jwtUtils.validateJwtToken(requestRefreshToken) || jwtUtils.isTokenExpired(requestRefreshToken)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Refresh token is expired!"));
        }

        String username = jwtUtils.getUserNameFromJwtToken(requestRefreshToken);
        String newAccessToken = jwtUtils.generateTokenFromUsername(username);
        
        return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, requestRefreshToken));
    }
}
