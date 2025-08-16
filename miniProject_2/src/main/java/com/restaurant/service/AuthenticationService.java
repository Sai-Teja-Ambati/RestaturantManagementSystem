package org.restaurant.service;

import org.restaurant.dto.request.CreateUserRequest;
import org.restaurant.dto.request.LoginRequest;
import org.restaurant.dto.response.JwtAuthenticationResponse;
import org.restaurant.dto.response.UserResponse;
import org.restaurant.enums.UserRole;

/**
 * Service interface for authentication operations
 */
public interface AuthenticationService {

    /**
     * Register a new user with non-privileged roles only
     * @param request User registration request
     * @return JWT authentication response
     */
    JwtAuthenticationResponse register(CreateUserRequest request);

    /**
     * Register a privileged user (Admin/Manager) - Admin only
     * @param request User registration request
     * @return JWT authentication response
     */
    JwtAuthenticationResponse registerAdmin(CreateUserRequest request);

    /**
     * Authenticate user and return JWT token
     * @param request Login request
     * @return JWT authentication response
     */
    JwtAuthenticationResponse login(LoginRequest request);

    /**
     * Refresh JWT token
     * @param refreshToken Refresh token
     * @return JWT authentication response
     */
    JwtAuthenticationResponse refreshToken(String refreshToken);

    /**
     * Change user password
     * @param currentPassword Current password
     * @param newPassword New password
     */
    void changePassword(String currentPassword, String newPassword);

    /**
     * Reset user password (admin function)
     * @param userId User ID
     * @param newPassword New password
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * Get current authenticated user
     * @return User response
     */
    UserResponse getCurrentUser();

    /**
     * Check if current user has specific role
     * @param role User role to check
     * @return true if user has the role
     */
    boolean currentUserHasRole(UserRole role);

    /**
     * Check if current user is admin
     * @return true if user is admin
     */
    boolean isCurrentUserAdmin();

    /**
     * Check if current user is manager
     * @return true if user is manager
     */
    boolean isCurrentUserManager();

    /**
     * Check if current user is waiter
     * @return true if user is waiter
     */
    boolean isCurrentUserWaiter();

    /**
     * Check if current user is chef
     * @return true if user is chef
     */
    boolean isCurrentUserChef();

    /**
     * Logout user
     */
    void logout();

    /**
     * Validate if user can perform action based on role
     * @param requiredRole Required role
     */
    void validateUserPermission(UserRole requiredRole);

    /**
     * Get current user's ID
     * @return Current user ID
     */
    Long getCurrentUserId();
}