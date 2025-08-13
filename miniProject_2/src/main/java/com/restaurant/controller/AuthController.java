package com.restaurant.controller;

import com.restaurant.dto.AuthRequest;
import com.restaurant.dto.AuthResponse;
import com.restaurant.dto.RegisterRequest;
import com.restaurant.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Authentication failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}