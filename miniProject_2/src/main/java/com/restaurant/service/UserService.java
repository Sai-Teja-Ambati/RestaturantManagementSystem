package org.restaurant.service;

import org.restaurant.dto.request.CreateUserRequest;
import org.restaurant.dto.response.UserResponse;
import org.restaurant.enums.UserRole;

import java.util.List;

/**
 * Service interface for user management operations
 */
public interface UserService {

    /**
     * Create a new user
     * @param request User creation request
     * @return User response
     */
    UserResponse createUser(CreateUserRequest request);

    /**
     * Get all users
     * @return List of user responses
     */
    List<UserResponse> getAllUsers();

    /**
     * Get user by ID
     * @param userId User ID
     * @return User response
     */
    UserResponse getUserById(Long userId);

    /**
     * Update user
     * @param userId User ID
     * @param request User update request
     * @return Updated user response
     */
    UserResponse updateUser(Long userId, CreateUserRequest request);

    /**
     * Delete user
     * @param userId User ID
     */
    void deleteUser(Long userId);

    /**
     * Get users by role
     * @param role User role
     * @return List of user responses
     */
    List<UserResponse> getUsersByRole(UserRole role);

    /**
     * Get all waiters
     * @return List of waiter responses
     */
    List<UserResponse> getAllWaiters();

    /**
     * Get all chefs
     * @return List of chef responses
     */
    List<UserResponse> getAllChefs();

    /**
     * Get all managers
     * @return List of manager responses
     */
    List<UserResponse> getAllManagers();

    /**
     * Check if user exists
     * @param userId User ID
     * @return true if user exists
     */
    boolean userExists(Long userId);

    /**
     * Get user by username
     * @param username Username
     * @return User response
     */
    UserResponse getUserByUsername(String username);

    /**
     * Get user entity by ID (for internal use)
     * @param userId User ID
     * @return User entity
     */
    org.restaurant.entities.User getUserEntityById(Long userId);
}