package zeta.foods.simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeta.foods.model.User;
import zeta.foods.service.AuthService;
import zeta.foods.service.impl.PostgresAuthServiceImpl;

/**
 * A simulation tool for testing the authentication system
 * without requiring manual input.
 */
public class AuthSimulation {
    private static final Logger logger = LoggerFactory.getLogger(AuthSimulation.class);
    private final AuthService authService;

    public AuthSimulation(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Run a simulation to test user login with different account types.
     */
    public void runLoginSimulation() {
        logger.info("=== Starting Authentication Simulation ===");

        // Test admin login
        logger.info("Testing admin login...");
        testLogin("ambatisaiteja123@gmail.com", "admin123", "Admin");

        // Test waiter login
        logger.info("Testing waiter login...");
        testLogin("waiter1@restaurant.com", "12345678", "Waiter");

        // Test customer login
        logger.info("Testing customer login...");
        testLogin("customer1@example.com", "12345678", "Customer");

        // Test invalid login
        logger.info("Testing invalid login...");
        testLogin("nonexistent@example.com", "wrongpassword", "Invalid User");

        logger.info("=== Authentication Simulation Completed ===");
    }

    /**
     * Helper method to test a specific login
     */
    private void testLogin(String email, String password, String userType) {
        logger.info("Attempting to log in as {}: {}", userType, email);
        User user = authService.authenticateUser(email, password);

        if (user != null) {
            logger.info("✅ Login successful for {}", userType);
            logger.info("User details - ID: {}, Username: {}, Role: {}",
                    user.getId(), user.getUsername(), user.getRole());

            // Display appropriate menu based on user role
            if (user.isAdmin()) {
                displayAdminMenu();
            } else if (user.isWaiter()) {
                displayWaiterMenu();
            } else {
                displayCustomerMenu();
            }

            logger.info("Session ended for {}", userType);
        } else {
            logger.info("❌ Login failed for {} with email: {}", userType, email);
        }

        logger.info("-----------------------------------");
    }

    /**
     * Display admin menu options
     */
    private void displayAdminMenu() {
        logger.info("📋 ADMIN MENU:");
        logger.info("1. Manage Users");
        logger.info("2. View System Reports");
        logger.info("3. Update Inventory");
        logger.info("4. Manage Menu Items");
        logger.info("5. Exit");

        // Simulate admin selecting an option
        logger.info("Admin selected: View System Reports");
        logger.info("Generating reports...");
        logger.info("Total Orders: 243");
        logger.info("Revenue: $5,678.90");
        logger.info("Popular Items: Pasta Carbonara, Chicken Parmesan");
    }

    /**
     * Display waiter menu options
     */
    private void displayWaiterMenu() {
        logger.info("📋 WAITER MENU:");
        logger.info("1. Take New Order");
        logger.info("2. View Table Status");
        logger.info("3. Process Payment");
        logger.info("4. Exit");

        // Simulate waiter selecting an option
        logger.info("Waiter selected: View Table Status");
        logger.info("Table 1: Occupied (4 guests) - Order in progress");
        logger.info("Table 2: Available");
        logger.info("Table 3: Reserved (19:00)");
        logger.info("Table 4: Occupied (2 guests) - Waiting for food");
    }

    /**
     * Display customer menu options
     */
    private void displayCustomerMenu() {
        logger.info("📋 CUSTOMER MENU:");
        logger.info("1. View Menu");
        logger.info("2. Place Order");
        logger.info("3. View Order Status");
        logger.info("4. Exit");

        // Simulate customer selecting an option
        logger.info("Customer selected: View Menu");
        logger.info("APPETIZERS:");
        logger.info("- Garlic Bread ($4.99)");
        logger.info("- Calamari ($8.99)");
        logger.info("MAIN COURSES:");
        logger.info("- Pasta Carbonara ($14.99)");
        logger.info("- Grilled Salmon ($19.99)");
    }
}
