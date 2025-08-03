package zeta.foods.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeta.foods.model.User;
import zeta.foods.service.AuthService;
import zeta.foods.utils.DatabaseUtil;

import java.sql.*;
import java.util.Scanner;

public class PostgresAuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(PostgresAuthServiceImpl.class);

    @Override
    public User authenticateUser(String email, String password) {
        // Log authentication attempt but mask password for security
        logger.info("Attempting to authenticate user: '{}'", email);
        logger.info("Password length: {}, Password value: '{}'", password != null ? password.length() : "null", password);

        // Print each character code to check for hidden/special characters
        if (password != null) {
            StringBuilder charCodes = new StringBuilder("Password char codes: ");
            for (char c : password.toCharArray()) {
                charCodes.append((int)c).append(",");
            }
            logger.info(charCodes.toString());
        }

        // Debug: directly check if user exists with this exact email and password
        try (Connection debugConn = DatabaseUtil.getConnection();
             Statement debugStmt = debugConn.createStatement()) {

            // First get all users matching the email only
            String emailOnlyQuery = "SELECT * FROM users WHERE email = '" + email + "'";
            try (ResultSet emailRs = debugStmt.executeQuery(emailOnlyQuery)) {
                if (emailRs.next()) {
                    String dbEmail = emailRs.getString("email");
                    String dbPassword = emailRs.getString("password");

                    logger.info("Found user with email: '{}', DB password: '{}'", dbEmail, dbPassword);
                    logger.info("Password length from DB: {}", dbPassword.length());

                    // Print each character code of DB password
                    StringBuilder dbCharCodes = new StringBuilder("DB Password char codes: ");
                    for (char c : dbPassword.toCharArray()) {
                        dbCharCodes.append((int)c).append(",");
                    }
                    logger.info(dbCharCodes.toString());

                    // Direct character-by-character comparison
                    logger.info("Direct password comparison result: {}", dbPassword.equals(password));

                    if (!dbPassword.equals(password)) {
                        logger.info("Password mismatch details:");
                        if (dbPassword.length() != password.length()) {
                            logger.info("Length differs: DB={}, Input={}", dbPassword.length(), password.length());
                        } else {
                            for (int i = 0; i < dbPassword.length(); i++) {
                                if (dbPassword.charAt(i) != password.charAt(i)) {
                                    logger.info("Mismatch at position {}: DB='{}' ({}), Input='{}' ({})",
                                        i, dbPassword.charAt(i), (int)dbPassword.charAt(i),
                                        password.charAt(i), (int)password.charAt(i));
                                }
                            }
                        }
                    }
                } else {
                    logger.warn("No user found with email: '{}'", email);
                }
            }

            // Now try the full authentication query
//            String debugQuery = "SELECT * FROM users WHERE email = '" + email + "' AND password = '" + password + "'";
            String debugQuery = "SELECT * FROM users WHERE email = 'ambatisaiteja123@gmail.com' AND password = 'admin123'";
            logger.info("Executing direct query: {}", debugQuery);


            try (ResultSet debugRs = debugStmt.executeQuery(debugQuery)) {
                if (debugRs.next()) {
                    logger.info("DEBUG - Direct query found matching user!");
                } else {
                    logger.warn("DEBUG - Direct query found NO matching user!");
                }
            }
        } catch (SQLException e) {
            logger.error("DEBUG - Error in diagnostic queries: {}", e.getMessage());
        }

        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        logger.info("Preparing to execute authentication query for email: {}, password: {}", email, password);
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            logger.debug("Executing authentication query for email: {}", email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));

                    // Update last login time
                    updateLastLogin(user.getId());

                    logger.info("User authenticated successfully: {}", email);
                    return user;
                } else {
                    logger.warn("No user found with email: {} and provided password", email);
                }
            }

            logger.warn("Authentication failed for user: {}", email);
            return null;

        } catch (SQLException e) {
            logger.error("Database error during authentication: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Interactive console login method that handles the scanner input directly
     * This is useful when running in Docker or other environments where
     * standard input handling might be tricky
     */
    public User consoleLogin() {
        // Use System.console() if available, otherwise fall back to Scanner
        User authenticatedUser = null;
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        // Print a visual separator to make the prompt more visible
        System.out.println("\n*************************************");
        System.out.println("*          LOGIN PROMPT             *");
        System.out.println("*************************************\n");

        while (attempts < MAX_ATTEMPTS && authenticatedUser == null) {
            try {
                String email = null;
                String password = null;

                // Try to use console if available (better for password input)
                if (System.console() != null) {
                    System.out.print("Enter email: ");
                    email = System.console().readLine();
                    System.out.print("Enter password: ");
                    password = new String(System.console().readLine());
                } else {
                    // Fall back to Scanner with flush to ensure prompt is visible
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Enter email: ");
                    System.out.flush();
                    email = scanner.nextLine().trim();

                    System.out.print("Enter password: ");
                    System.out.flush();
                    password = scanner.nextLine().trim();
                }

                if (email == null || email.isEmpty()) {
                    System.out.println("Email cannot be empty. Please try again.");
                    continue;
                }

                // Handle special commands
                if ("exit".equalsIgnoreCase(email)) {
                    logger.info("Login process aborted by user");
                    break;
                } else if ("simulate".equalsIgnoreCase(email)) {
                    logger.info("Simulation requested from console login");
                    return null; // Special case to trigger simulation
                }

                // Use the provided credentials (not hardcoded values)
                authenticatedUser = authenticateUser(email, password);

                // Handle authentication result
                if (authenticatedUser == null) {
                    attempts++;
                    logger.warn("Login failed. Attempt {} of {}", attempts, MAX_ATTEMPTS);
                    if (attempts < MAX_ATTEMPTS) {
                        System.out.println("Invalid email or password. Please try again.");
                    }
                } else {
                    System.out.println("\nWelcome, " + authenticatedUser.getUsername() + "!");
                    System.out.println("You are logged in as: " + authenticatedUser.getRole());
                    displayMenuForRole(authenticatedUser.getRole());
                }
            } catch (Exception e) {
                logger.error("Error during console login: {}", e.getMessage(), e);
                System.out.println("An error occurred. Please try again.");
                attempts++;
            }
        }

        if (authenticatedUser == null && attempts >= MAX_ATTEMPTS) {
            System.out.println("Maximum login attempts exceeded. Please try again later.");
        }

        return authenticatedUser;
    }

    /**
     * Display appropriate menu based on user role
     */
    private void displayMenuForRole(String role) {
        System.out.println("\n=== " + role.toUpperCase() + " MENU ===");

        switch (role.toLowerCase()) {
            case "admin":
                System.out.println("1. Manage Users");
                System.out.println("2. View System Reports");
                System.out.println("3. Update Inventory");
                System.out.println("4. Manage Menu Items");
                break;

            case "waiter":
                System.out.println("1. Take New Order");
                System.out.println("2. View Table Status");
                System.out.println("3. Process Payment");
                break;

            case "customer":
                System.out.println("1. View Menu");
                System.out.println("2. Place Order");
                System.out.println("3. View Order Status");
                break;

            default:
                System.out.println("No specific menu available for this role.");
        }

        System.out.println("0. Logout");
        System.out.print("\nPlease enter your choice: ");
    }

    @Override
    public boolean isAdmin(String email) {
        return checkUserRole(email, "admin");
    }

    @Override
    public boolean isWaiter(String email) {
        return checkUserRole(email, "waiter");
    }

    private boolean checkUserRole(String email, String role) {
        String sql = "SELECT role FROM users WHERE email = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && role.equals(rs.getString("role"));
            }

        } catch (SQLException e) {
            logger.error("Database error checking user role: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public User createUser(String username, String email, String password, String role) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Store the password as plain text
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setRole(role);

                    logger.info("Created new user: {} with role: {}", email, role);
                    return user;
                }
            }

            return null;

        } catch (SQLException e) {
            logger.error("Database error creating user: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean updateLastLogin(Long userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("Database error updating last login: {}", e.getMessage(), e);
            return false;
        }
    }
}
