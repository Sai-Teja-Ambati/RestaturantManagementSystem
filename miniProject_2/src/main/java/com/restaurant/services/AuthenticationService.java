package org.restaurant.services;

import org.restaurant.dto.request.CreateUserRequest;
import org.restaurant.dto.request.LoginRequest;
import org.restaurant.dto.response.JwtAuthenticationResponse;
import org.restaurant.dto.response.UserResponse;
import org.restaurant.entities.User;
import org.restaurant.enums.UserRole;
import org.restaurant.exceptions.ResourceAlreadyExistsException;
import org.restaurant.exceptions.BusinessLogicException;
import org.restaurant.repositories.UserRepository;
import org.restaurant.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Register a new user
     * Note: Public registration is restricted to non-privileged roles only.
     * Admin and Manager accounts can only be created by existing admins.
     */
    public JwtAuthenticationResponse register(CreateUserRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username '" + request.getUsername() + "' is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
        }

        // Security: Restrict public registration to non-privileged roles only
        UserRole assignedRole = UserRole.CUSTOMER; // Default to least privileged role
        
        if (request.getRole() != null) {
            // Only allow registration for safe, non-privileged roles
            if (request.getRole() == UserRole.CUSTOMER || 
                request.getRole() == UserRole.WAITER || 
                request.getRole() == UserRole.CHEF) {
                assignedRole = request.getRole();
            } else if (request.getRole() == UserRole.ADMIN || request.getRole() == UserRole.MANAGER) {
                // Log security attempt and reject privileged role assignment
                throw new BusinessLogicException("Cannot register with privileged role '" + request.getRole() + 
                    "'. Admin and Manager accounts must be created by existing administrators.");
            }
        }

        // Create new user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(assignedRole); // Use the validated role
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Save user
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String jwt = jwtUtils.generateTokenFromUsername(savedUser.getUsername());

        // Create user response
        UserResponse userResponse = convertToUserResponse(savedUser);

        return new JwtAuthenticationResponse(jwt, userResponse);
    }

    /**
     * Register a privileged user (Admin/Manager) - Admin only
     * This endpoint allows existing admins to create Admin and Manager accounts
     */
    public JwtAuthenticationResponse registerAdmin(CreateUserRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username '" + request.getUsername() + "' is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
        }

        // Validate that only privileged roles are being created through this endpoint
        if (request.getRole() == null) {
            throw new BusinessLogicException("Role is required for admin registration");
        }

        if (request.getRole() != UserRole.ADMIN && request.getRole() != UserRole.MANAGER) {
            throw new BusinessLogicException("This endpoint is only for creating Admin and Manager accounts. " +
                "Use regular registration for other roles.");
        }

        // Create new privileged user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole()); // Allow Admin/Manager roles
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Save user
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String jwt = jwtUtils.generateTokenFromUsername(savedUser.getUsername());

        // Create user response
        UserResponse userResponse = convertToUserResponse(savedUser);

        return new JwtAuthenticationResponse(jwt, userResponse);
    }

    /**
     * Authenticate user and return JWT token
     */
    public JwtAuthenticationResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Get user details
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BusinessLogicException("User not found"));

            UserResponse userResponse = convertToUserResponse(user);

            return new JwtAuthenticationResponse(jwt, userResponse);

        } catch (BadCredentialsException e) {
            throw new BusinessLogicException("Invalid username or password");
        }
    }

    /**
     * Refresh JWT token
     */
    public JwtAuthenticationResponse refreshToken(String refreshToken) {
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new BusinessLogicException("Invalid refresh token");
        }

        String username = jwtUtils.getUsernameFromJwtToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessLogicException("User not found"));

        // Generate new access token
        String newAccessToken = jwtUtils.generateTokenFromUsername(username);

        UserResponse userResponse = convertToUserResponse(user);

        return new JwtAuthenticationResponse(newAccessToken, userResponse);
    }

    /**
     * Change user password
     */
    public void changePassword(String currentPassword, String newPassword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessLogicException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessLogicException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Reset user password (admin function)
     */
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Get current authenticated user
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessLogicException("User not found"));

        return convertToUserResponse(user);
    }

    /**
     * Check if current user has specific role
     */
    @Transactional(readOnly = true)
    public boolean currentUserHasRole(UserRole role) {
        try {
            UserResponse currentUser = getCurrentUser();
            return currentUser.getRole().equals(role);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user is admin
     */
    @Transactional(readOnly = true)
    public boolean isCurrentUserAdmin() {
        return currentUserHasRole(UserRole.ADMIN);
    }

    /**
     * Check if current user is manager
     */
    @Transactional(readOnly = true)
    public boolean isCurrentUserManager() {
        return currentUserHasRole(UserRole.MANAGER);
    }

    /**
     * Check if current user is waiter
     */
    @Transactional(readOnly = true)
    public boolean isCurrentUserWaiter() {
        return currentUserHasRole(UserRole.WAITER);
    }

    /**
     * Check if current user is chef
     */
    @Transactional(readOnly = true)
    public boolean isCurrentUserChef() {
        return currentUserHasRole(UserRole.CHEF);
    }

    /**
     * Logout user (invalidate token on client side)
     * Note: JWT tokens are stateless, so we just return success
     * In a production environment, you might want to maintain a blacklist of tokens
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Validate if user can perform action based on role
     */
    public void validateUserPermission(UserRole requiredRole) {
        UserResponse currentUser = getCurrentUser();

        // Admin can do everything
        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }

        // Manager can do most things except admin-only functions
        if (currentUser.getRole() == UserRole.MANAGER && requiredRole != UserRole.ADMIN) {
            return;
        }

        // Check exact role match for other roles
        if (!currentUser.getRole().equals(requiredRole)) {
            throw new BusinessLogicException("Insufficient permissions to perform this action");
        }
    }

    /**
     * Get current user's ID
     */
    @Transactional(readOnly = true)
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    // Helper method to convert User to UserResponse
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getUserId());
        response.setName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setUsername(user.getUsername());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}