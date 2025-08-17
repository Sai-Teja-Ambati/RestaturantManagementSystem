package org.restaurant.service;

import org.restaurant.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Custom UserDetails service interface extending Spring Security's UserDetailsService
 * This interface provides additional methods for user authentication and validation
 */
public interface CustomUserDetailsService extends UserDetailsService {

    /**
     * Load user by ID
     * @param id User ID
     * @return UserDetails
     * @throws UsernameNotFoundException if user not found
     */
    UserDetails loadUserById(Long id) throws UsernameNotFoundException;

    /**
     * Check if username exists
     * @param username Username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     * @param email Email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find user by email
     * @param email Email to search for
     * @return User entity
     * @throws UsernameNotFoundException if user not found
     */
    User findByEmail(String email) throws UsernameNotFoundException;

    /**
     * Find user by username
     * @param username Username to search for
     * @return User entity
     * @throws UsernameNotFoundException if user not found
     */
    User findByUsername(String username) throws UsernameNotFoundException;
}