package com.restaurant.service.impl;

import com.restaurant.dto.AuthRequest;
import com.restaurant.dto.AuthResponse;
import com.restaurant.dto.RegisterRequest;
import com.restaurant.entity.User;
import com.restaurant.service.AuthService;
import com.restaurant.service.JwtService;
import com.restaurant.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());
        try {
            User user = userService.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getRole()
            );

            String jwtToken = jwtService.generateToken(user);
            log.info("User registered successfully: {} with role: {}", user.getUsername(), user.getRole());
            
            return AuthResponse.builder()
                    .accessToken(jwtToken)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .message("User registered successfully")
                    .build();
        } catch (Exception e) {
            log.error("Registration failed for user: {}. Error: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        log.info("Attempting to authenticate user: {}", request.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            userService.updateLastLogin(user.getUsername());
            
            String jwtToken = jwtService.generateToken(user);
            log.info("User authenticated successfully: {} with role: {}", user.getUsername(), user.getRole());
            
            return AuthResponse.builder()
                    .accessToken(jwtToken)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .message("Authentication successful")
                    .build();
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}. Reason: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException("Authentication failed: Invalid credentials");
        }
    }
}