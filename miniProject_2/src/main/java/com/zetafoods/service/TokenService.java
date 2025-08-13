package com.zetafoods.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {
    String generateToken(String email, String role);

    String extractUserName(String token);

    boolean validateToken(String token, UserDetails userDetails);
}
