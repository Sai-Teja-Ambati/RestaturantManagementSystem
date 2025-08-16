package org.restaurant.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restaurant.dto.request.CreateUserRequest;
import org.restaurant.dto.request.LoginRequest;
import org.restaurant.dto.response.JwtAuthenticationResponse;
import org.restaurant.dto.response.UserResponse;
import org.restaurant.dto.response.SuccessResponse;
import org.restaurant.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * User login
     * POST /auth/login
     * Public endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login attempt for user: {}", request.getUsername());
        JwtAuthenticationResponse response = authenticationService.login(request);
        logger.info("Login successful for user: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * User registration
     * POST /auth/register
     * Public endpoint (or restricted based on business requirements)
     */
    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> register(@Valid @RequestBody CreateUserRequest request) {
        logger.info("User registration attempt for: {}", request.getUsername());
        JwtAuthenticationResponse response = authenticationService.register(request);
        logger.info("User registered successfully: {}", request.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Admin registration for privileged accounts
     * POST /auth/register-admin
     * Required: ADMIN role only
     */
    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JwtAuthenticationResponse> registerAdmin(@Valid @RequestBody CreateUserRequest request) {
        logger.info("Admin registration attempt for: {} with role: {}", request.getUsername(), request.getRole());
        JwtAuthenticationResponse response = authenticationService.registerAdmin(request);
        logger.info("Admin registered successfully: {} with role: {}", request.getUsername(), request.getRole());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Refresh JWT token
     * POST /auth/refresh
     * Requires valid refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        logger.info("Refresh token request received");
        JwtAuthenticationResponse response = authenticationService.refreshToken(request.getRefreshToken());
        logger.info("Refresh token successful");
        return ResponseEntity.ok(response);
    }

    /**
     * Get current user profile
     * GET /auth/me
     * Requires authentication
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse user = authenticationService.getCurrentUser();
        logger.info("Fetched current user profile: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }

    /**
     * Change password
     * PUT /auth/change-password
     * Requires authentication
     */
    @PutMapping("/change-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<SuccessResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        logger.info("Password change request received");
        authenticationService.changePassword(request.getCurrentPassword(), request.getNewPassword());
        logger.info("Password changed successfully");
        return ResponseEntity.ok(new SuccessResponse("Password changed successfully"));
    }

    /**
     * Reset user password (admin only)
     * PUT /auth/reset-password/{userId}
     * Requires ADMIN role
     */
    @PutMapping("/reset-password/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> resetPassword(@PathVariable Long userId,
                                                         @RequestBody ResetPasswordRequest request) {
        logger.info("Password reset request for user ID: {}", userId);
        authenticationService.resetPassword(userId, request.getNewPassword());
        logger.info("Password reset successfully for user ID: {}", userId);
        return ResponseEntity.ok(new SuccessResponse("Password reset successfully"));
    }

    /**
     * Logout user
     * POST /auth/logout
     * Requires authentication
     */
    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<SuccessResponse> logout() {
        logger.info("Logout request received");
        authenticationService.logout();
        logger.info("User logged out successfully");
        return ResponseEntity.ok(new SuccessResponse("Logged out successfully"));
    }

    /**
     * Check if current user is admin
     * GET /auth/is-admin
     * Requires authentication
     */
    @GetMapping("/is-admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<Boolean> isCurrentUserAdmin() {
        boolean isAdmin = authenticationService.isCurrentUserAdmin();
        logger.info("Checked if user is admin: {}", isAdmin);
        return ResponseEntity.ok(isAdmin);
    }

    /**
     * Check if current user is manager
     * GET /auth/is-manager
     * Requires authentication
     */
    @GetMapping("/is-manager")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<Boolean> isCurrentUserManager() {
        boolean isManager = authenticationService.isCurrentUserManager();
        logger.info("Checked if user is manager: {}", isManager);
        return ResponseEntity.ok(isManager);
    }

    /**
     * Check if current user is waiter
     * GET /auth/is-waiter
     * Requires authentication
     */
    @GetMapping("/is-waiter")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<Boolean> isCurrentUserWaiter() {
        boolean isWaiter = authenticationService.isCurrentUserWaiter();
        logger.info("Checked if user is waiter: {}", isWaiter);
        return ResponseEntity.ok(isWaiter);
    }

    /**
     * Check if current user is chef
     * GET /auth/is-chef
     * Requires authentication
     */
    @GetMapping("/is-chef")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<Boolean> isCurrentUserChef() {
        boolean isChef = authenticationService.isCurrentUserChef();
        logger.info("Checked if user is chef: {}", isChef);
        return ResponseEntity.ok(isChef);
    }

    /**
     * Health check endpoint
     * GET /auth/health
     * Public endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        logger.debug("Health check endpoint called");
        return ResponseEntity.ok("OK");
    }

    // Inner classes for request DTOs

    public static class RefreshTokenRequest {
        @NotBlank(message = "Refresh token is required")
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    public static class ChangePasswordRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        private String newPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    public static class ResetPasswordRequest {
        @NotBlank(message = "New password is required")
        private String newPassword;

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}