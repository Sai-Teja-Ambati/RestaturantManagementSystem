package zeta.foods.service;

import zeta.foods.model.User;

public interface AuthService {
    /**
     * Authenticate a user with email and password
     * @param email User's email
     * @param password User's password
     * @return User if authentication successful, null otherwise
     */
    User authenticateUser(String email, String password);

    /**
     * Check if a user is an admin
     * @param email User's email
     * @return true if admin, false otherwise
     */
    boolean isAdmin(String email);

    /**
     * Check if a user is a waiter
     * @param email User's email
     * @return true if waiter, false otherwise
     */
    boolean isWaiter(String email);

    /**
     * Create a new user account
     * @param username Username
     * @param email Email
     * @param password Password (will be hashed)
     * @param role Role (admin, waiter, customer)
     * @return Created user or null if creation failed
     */
    User createUser(String username, String email, String password, String role);

    /**
     * Register a new user account (primarily for customers)
     * @param username Username
     * @param email Email
     * @param password Password
     * @param role User role
     * @return User if registration successful, null otherwise
     */
    User registerUser(String username, String email, String password, String role);

    /**
     * Updates the last login time for a user
     * @param userId The user ID
     * @return true if successful, false otherwise
     */
    boolean updateLastLogin(Long userId);
}
