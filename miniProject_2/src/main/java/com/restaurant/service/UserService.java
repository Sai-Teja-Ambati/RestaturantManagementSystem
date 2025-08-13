package com.restaurant.service;

import com.restaurant.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    
    User createUser(String username, String email, String password, User.Role role);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findById(Long id);
    
    List<User> findByRole(User.Role role);
    
    List<User> getAllUsers();
    
    User updateLastLogin(String username);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    long countByRole(User.Role role);
    
    User save(User user);
    
    void deleteById(Long id);
}