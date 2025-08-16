package org.restaurant.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restaurant.dto.request.CreateUserRequest;
import org.restaurant.dto.response.UserResponse;
import org.restaurant.dto.response.SuccessResponse;
import org.restaurant.enums.UserRole;
import org.restaurant.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Create a new user
     * POST /users
     * Required: ADMIN role
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        logger.info("Creating new user with username: {} and role: {}", request.getUsername(), request.getRole());
        UserResponse createdUser = userService.createUser(request);
        logger.info("User created successfully with ID: {}", createdUser.getId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Get all users
     * GET /users
     * Required: ADMIN or MANAGER role
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        logger.info("Fetching all users");
        List<UserResponse> users = userService.getAllUsers();
        logger.info("Retrieved {} users", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     * GET /users/{id}
     * Required: ADMIN or MANAGER role
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        logger.info("Fetching user by ID: {}", id);
        UserResponse user = userService.getUserById(id);
        logger.info("User details: {}", user);
        return ResponseEntity.ok(user);
    }

    /**
     * Update user by ID
     * PUT /users/{id}
     * Required: ADMIN or MANAGER role
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody CreateUserRequest request) {
        logger.info("Updating user with ID: {}. New username: {}, role: {}", id, request.getUsername(), request.getRole());
        UserResponse updatedUser = userService.updateUser(id, request);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete user by ID
     * DELETE /users/{id}
     * Required: ADMIN role only
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        logger.info("User deleted successfully");
        return ResponseEntity.ok(new SuccessResponse("User deleted successfully"));
    }

    /**
     * Get users by role
     * GET /users/role/{role}
     * Required: ADMIN or MANAGER role
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable UserRole role) {
        logger.info("Fetching users with role: {}", role);
        List<UserResponse> users = userService.getUsersByRole(role);
        logger.info("Retrieved {} users with role {}", users.size(), role);
        return ResponseEntity.ok(users);
    }

    /**
     * Get all waiters
     * GET /users/waiters
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/waiters")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CHEF')")
    public ResponseEntity<List<UserResponse>> getAllWaiters() {
        logger.info("Fetching all waiters");
        List<UserResponse> waiters = userService.getAllWaiters();
        logger.info("Retrieved {} waiters", waiters.size());
        return ResponseEntity.ok(waiters);
    }

    /**
     * Get all chefs
     * GET /users/chefs
     * Required: ADMIN, MANAGER, or CHEF role
     */
    @GetMapping("/chefs")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CHEF')")
    public ResponseEntity<List<UserResponse>> getAllChefs() {
        logger.info("Fetching all chefs");
        List<UserResponse> chefs = userService.getAllChefs();
        logger.info("Retrieved {} chefs", chefs.size());
        return ResponseEntity.ok(chefs);
    }

    /**
     * Get all managers
     * GET /users/managers
     * Required: ADMIN role
     */
    @GetMapping("/managers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllManagers() {
        logger.info("Fetching all managers");
        List<UserResponse> managers = userService.getAllManagers();
        logger.info("Retrieved {} managers", managers.size());
        return ResponseEntity.ok(managers);
    }

    /**
     * Check if user exists
     * GET /users/{id}/exists
     * Required: ADMIN or MANAGER role
     */
    @GetMapping("/{id}/exists")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Boolean> userExists(@PathVariable Long id) {
        logger.info("Checking if user exists with ID: {}", id);
        boolean exists = userService.userExists(id);
        logger.info("User exists: {}", exists);
        return ResponseEntity.ok(exists);
    }
}