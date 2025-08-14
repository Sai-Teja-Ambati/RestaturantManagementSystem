package com.restaurant.service;

import com.restaurant.entity.User;
import com.restaurant.enums.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    
    User createUser(String username, String name, String email, String password, Role role);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findById(Long id);
    
    List<User> findByRole(Role role);
    
    List<User> getAllUsers();
    
    User updateLastLogin(String username);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    long countByRole(Role role);
    
    User save(User user);
    
    void deleteById(Long id);
}