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

        if (password != null) {
            StringBuilder charCodes = new StringBuilder("Password char codes: ");
            for (char c : password.toCharArray()) {
                charCodes.append((int)c).append(",");
            }
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

                    // Print each character code of DB password
                    StringBuilder dbCharCodes = new StringBuilder("DB Password char codes: ");
                    for (char c : dbPassword.toCharArray()) {
                        dbCharCodes.append((int)c).append(",");
                    }

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

            String debugQuery = "SELECT * FROM users WHERE email = '" + email + "' AND password = '" + password + "'";

            try (ResultSet debugRs = debugStmt.executeQuery(debugQuery)) {
                if (debugRs.next()) {
                } else {
                    logger.warn("DEBUG - Direct query found NO matching user!");
                }
            }
        } catch (SQLException e) {
            logger.error("DEBUG - Error in diagnostic queries: {}", e.getMessage());
        }

        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

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
        System.out.println("*          WELCOME                  *");
        System.out.println("*************************************\n");
        System.out.println("1. Login");
        System.out.println("2. Sign Up (New Customer)");
        System.out.println("0. Exit");
        System.out.print("\nSelect an option: ");
        
        Scanner scanner = new Scanner(System.in);
        String option = scanner.nextLine().trim();
        
        switch (option) {
            case "1":
                // Login process
                while (attempts < MAX_ATTEMPTS && authenticatedUser == null) {
                    try {
                        String email = null;
                        String password = null;

                        // Try to use console if available (better for password input)
                        if (System.console() != null) {
                            System.out.print("\nEnter email: ");
                            email = System.console().readLine();
                            System.out.print("Enter password: ");
                            password = new String(System.console().readLine());
                        } else {
                            // Fall back to Scanner with flush to ensure prompt is visible
                            System.out.print("\nEnter email: ");
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

                        // Use the provided credentials
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
                break;
                
            case "2":
                // Sign up process
                authenticatedUser = consoleSignup(scanner);
                if (authenticatedUser != null) {
                    System.out.println("\nWelcome, " + authenticatedUser.getUsername() + "!");
                    System.out.println("You are logged in as: " + authenticatedUser.getRole());
                    displayMenuForRole(authenticatedUser.getRole());
                }
                break;
                
            case "0":
                logger.info("User chose to exit");
                break;
                
            default:
                System.out.println("Invalid option. Please try again.");
        }

        return authenticatedUser;
    }
    
    /**
     * Interactive console signup method for new customers
     * 
     * @param scanner Scanner for reading user input
     * @return The newly created and authenticated User object, or null if signup failed
     */
    private User consoleSignup(Scanner scanner) {
        System.out.println("\n*************************************");
        System.out.println("*          NEW CUSTOMER SIGNUP      *");
        System.out.println("*************************************\n");
        
        try {
            // Get username
            System.out.print("Enter your name: ");
            String username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Name cannot be empty.");
                return null;
            }
            
            // Get and validate email
            String email = "";
            boolean validEmail = false;
            while (!validEmail) {
                System.out.print("Enter your email: ");
                email = scanner.nextLine().trim();
                
                if (email.isEmpty()) {
                    System.out.println("Email cannot be empty.");
                    continue;
                }
                
                if (!isValidEmail(email)) {
                    System.out.println("Invalid email format. Please enter a valid email address.");
                    continue;
                }
                
                if (userExists(email)) {
                    System.out.println("This email is already registered. Please use another email or log in.");
                    return null;
                }
                
                validEmail = true;
            }
            
            // Get and validate password
            String password = "";
            String confirmPassword = "";
            boolean validPassword = false;
            while (!validPassword) {
                System.out.print("Enter password (minimum 6 characters): ");
                password = scanner.nextLine().trim();
                
                if (password.length() < 6) {
                    System.out.println("Password must be at least 6 characters long.");
                    continue;
                }
                
                System.out.print("Confirm password: ");
                confirmPassword = scanner.nextLine().trim();
                
                if (!password.equals(confirmPassword)) {
                    System.out.println("Passwords do not match. Please try again.");
                    continue;
                }
                
                validPassword = true;
            }
            
            // Register the new user
            User newUser = registerUser(username, email, password, "customer");
            
            if (newUser != null) {
                System.out.println("\nRegistration successful! Your account has been created.");
                return newUser;
            } else {
                System.out.println("\nRegistration failed. Please try again later.");
            }
            
        } catch (Exception e) {
            logger.error("Error during console signup: {}", e.getMessage(), e);
            System.out.println("An error occurred during registration. Please try again later.");
        }
        
        return null;
    }
    
    /**
     * Display appropriate menu based on user role
     */
    private void displayMenuForRole(String role) {
        System.out.println("\n=== " + role.toUpperCase() + " MENU ===");

        switch (role.toLowerCase()) {
            case "admin":
//                System.out.println("1. Manage Users");
//                System.out.println("2. View System Reports");
                System.out.println("3. Fetch Current Inventory");
                System.out.println("4. Restore Current Inventory");
//                System.out.println("5. Manage Menu Items");
                break;

            case "waiter":
                System.out.println("1. Take New Order");
                System.out.println("2. Check Unserved Tables");
                break;

            case "customer":
                System.out.println("1. View Menu");
                System.out.println("2. Place Order for Take Away");
                System.out.println("3. View Order Status");
                System.out.println("4. Book Table");
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

    /**
     * Register a new user in the system
     * 
     * @param username The username for the new user
     * @param email The email address for the new user
     * @param password The password for the new user
     * @param role The role of the new user (usually "customer" for new registrations)
     * @return The newly created User object, or null if registration failed
     */
    public User registerUser(String username, String email, String password, String role) {
        logger.info("Attempting to register new user with email: {}", email);
        
        // Validate email format using regex
        if (!isValidEmail(email)) {
            logger.warn("Invalid email format: {}", email);
            return null;
        }
        
        // Check if user already exists
        if (userExists(email)) {
            logger.warn("User with email {} already exists", email);
            return null;
        }
        
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Create and return the new user
                    User newUser = new User();
                    newUser.setId(rs.getLong("id"));
                    newUser.setUsername(username);
                    newUser.setEmail(email);
                    newUser.setRole(role);
                    
                    logger.info("New user registered successfully: {}", email);
                    return newUser;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Database error during user registration: {}", e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * Check if a user with the given email already exists
     * 
     * @param email The email to check
     * @return true if the user exists, false otherwise
     */
    private boolean userExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error checking if user exists: {}", e.getMessage(), e);
        }
        
        return false;
    }
    
    /**
     * Validate email format using regex
     * 
     * @param email The email to validate
     * @return true if the email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        // Basic email validation regex pattern
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(emailRegex);
    }
}
