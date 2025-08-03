package zeta.foods;

import zeta.foods.model.User;
import zeta.foods.service.AuthService;
import zeta.foods.service.impl.PostgresAuthServiceImpl;
import zeta.foods.simulation.AuthSimulation;
import zeta.foods.utils.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static AuthService authService;

    public static void main(String[] args) {
        logger.info("Starting Restaurant Management System...");

        try {
            // Print database connection info
            String dbHost = System.getenv().getOrDefault("DB_HOST", "localhost");
            String dbPort = System.getenv().getOrDefault("DB_PORT", "5432");
            String dbName = System.getenv().getOrDefault("DB_NAME", "restaurant_management");

            logger.info("Database Configuration:");
            logger.info("  Host: {}", dbHost);
            logger.info("  Port: {}", dbPort);
            logger.info("  Database: {}", dbName);

            // Check for simulation mode from command line or environment
            boolean simulationMode = Boolean.parseBoolean(System.getenv().getOrDefault("SIMULATION_MODE", "false"));
            if (args.length > 0 && "simulate".equalsIgnoreCase(args[0])) {
                simulationMode = true;
            }

            // Test database connection
            DatabaseUtil.getConnection();
            logger.info("Database connection successful");

            // Initialize Auth Service
            authService = new PostgresAuthServiceImpl();
            logger.info("PostgreSQL Auth Service initialized");

            logger.info("Restaurant Management System is running successfully!");
            logger.info("Application is ready to serve requests on port 8080");

            // Run simulation or interactive mode
            if (simulationMode) {
                runSimulation();
            } else {
                startLoginProcess();
            }

        } catch (Exception e) {
            logger.error("Failed to start Restaurant Management System", e);
            System.exit(1);
        }
    }

    private static void runSimulation() {
        logger.info("=== Starting Authentication Simulation Mode ===");
        AuthSimulation simulation = new AuthSimulation(authService);
        simulation.runLoginSimulation();

        // Close database connection when finished
        DatabaseUtil.closeConnection();
        logger.info("Simulation completed. Exiting application.");
    }

    private static void startLoginProcess() {
        logger.info("=== Restaurant Management System Login ===");
        logger.info("Available test accounts:");
        logger.info("Admin: ambatisaiteja123@gmail.com (password: admin123)");
        logger.info("Waiters: waiter1@restaurant.com through waiter5@restaurant.com (password: 12345678)");
        logger.info("Customer: customer1@example.com (password: 12345678)");
        logger.info("Type 'simulate' to run automatic login simulation");
        logger.info("Type 'exit' to quit the application");

        try {
            // Use the enhanced console login method which is more reliable in Docker
            PostgresAuthServiceImpl authServiceImpl = (PostgresAuthServiceImpl) authService;
            User user = authServiceImpl.consoleLogin();

            if (user == null) {
                // Check if simulation was requested through the console login
                if ("simulate".equalsIgnoreCase(System.getenv().getOrDefault("LAST_COMMAND", ""))) {
                    runSimulation();
                }
            }

            // Keep application running until user selects exit
            Scanner scanner = new Scanner(System.in);
            boolean running = user != null;

            while (running) {
                String input = scanner.nextLine().trim();

                if ("0".equals(input) || "exit".equalsIgnoreCase(input)) {
                    logger.info("Logging out and exiting application");
                    running = false;
                } else {
                    // Handle menu selection based on user role
                    logger.info("Selected option: {}", input);
                    System.out.println("Processing your request...");
                    System.out.println("Feature will be implemented in future updates.");
                    System.out.print("Enter another option (0 to exit): ");
                }
            }

            scanner.close();
        } catch (Exception e) {
            logger.error("Error during login process: {}", e.getMessage(), e);
        } finally {
            // Close database connection when app exits
            DatabaseUtil.closeConnection();
        }
    }
}