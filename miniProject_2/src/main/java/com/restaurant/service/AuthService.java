package com.restaurant.service;

import com.restaurant.dto.AuthRequest;
import com.restaurant.dto.AuthResponse;
import com.restaurant.dto.RegisterRequest;

public interface AuthService {
    
    AuthResponse register(RegisterRequest request);
    
    AuthResponse authenticate(AuthRequest request);
}