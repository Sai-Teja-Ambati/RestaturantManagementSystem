package zeta.foods;

import zeta.foods.model.Order;
import zeta.foods.model.User;
import zeta.foods.service.AdminService;
import zeta.foods.service.AuthService;
import zeta.foods.service.CustomerService;
import zeta.foods.service.WaiterService;
import zeta.foods.service.impl.AdminServiceImpl;
import zeta.foods.service.impl.CustomerServiceImpl;
import zeta.foods.service.impl.PostgresAuthServiceImpl;
import zeta.foods.service.impl.WaiterServiceImpl;
import zeta.foods.simulation.AuthSimulation;
import zeta.foods.utils.DatabaseUtil;
import zeta.foods.utils.menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static AuthService authService;
    private static CustomerService customerService;
    private static AdminService adminService;
    private static WaiterService waiterService;

    // Banner to display on startup
    private static final String BANNER =
        "\n" +
        " ╔═══════════════════════════════════════════════════════════════╗\n" +
        " ║                                                               ║\n" +
        " ║    ███████╗███████╗ ██████╗  ██████╗ ██████╗ ███████╗         ║\n" +
        " ║    ╚══███╔╝██╔════╝██╔═══██╗██╔═══██╗██╔══██╗██╔════╝         ║\n" +
        " ║      ███╔╝ █████╗  ██║   ██║██║   ██║██║  ██║███████╗         ║\n" +
        " ║     ███╔╝  ██╔══╝  ██║   ██║██║   ██║██║  ██║╚════██║         ║\n" +
        " ║    ███████╗██║     ╚██████╔╝╚██████╔╝██████╔╝███████║         ║\n" +
        " ║    ╚══════╝╚═╝      ╚═════╝  ╚═════╝ ╚═════╝ ╚══════╝         ║\n" +
        " ║                                                               ║\n" +
        " ║                  RESTAURANT MANAGEMENT SYSTEM                 ║\n" +
        " ║                                                               ║\n" +
        " ╚═══════════════════════════════════════════════════════════════╝\n";

    public static void main(String[] args) {
        logger.info(BANNER);

        logger.info("Starting Restaurant Management System...");

        try {
            // Print database connection info
            String dbHost = System.getenv().getOrDefault("DB_HOST", "localhost");
            String dbPort = System.getenv().getOrDefault("DB_PORT", "5432");
            String dbName = System.getenv().getOrDefault("DB_NAME", "restaurant_management");

            // Check for simulation mode from command line or environment
            boolean simulationMode = Boolean.parseBoolean(System.getenv().getOrDefault("SIMULATION_MODE", "false"));
            if (args.length > 0 && "simulate".equalsIgnoreCase(args[0])) {
                simulationMode = true;
            }

            // Test database connection
            DatabaseUtil.getConnection();
            logger.info("Database connection successful");

            // Initialize services
            authService = new PostgresAuthServiceImpl();
            customerService = new CustomerServiceImpl();
            adminService = new AdminServiceImpl();
            waiterService = new WaiterServiceImpl();
            logger.info("Services initialized");

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

                    if (user.getRole().equalsIgnoreCase("customer")) {
                        switch (input) {
                            case "1":
                                customerService.displayRestaurantMenu(scanner);
                                break;
                            case "2":
                                processOrderPlacement(user, scanner);
                                break;
                            case "3":
                                viewOrderStatus(user, scanner);
                                break;
                            case "4":
                                bookTableForCustomer(user, scanner);
                                break;
                            default:
                                System.out.println("Invalid option. Please try again.");
                                break;
                        }
                    } else if (user.getRole().equalsIgnoreCase("waiter")) {
                        switch (input) {
                            case "1":
                                takeNewOrderAsWaiter(scanner);
                                break;
                            case "2":
                                checkUnservedTables();
                                break;
                            default:
                                System.out.println("Invalid option. Please try again.");
                                break;
                        }
                    } else if (user.getRole().equalsIgnoreCase("admin")) {
                        switch (input) {
                            case "1":
                                System.out.println("Manage Users - Feature will be implemented in future updates.");
                                break;
                            case "2":
                                System.out.println("View System Reports - Feature will be implemented in future updates.");
                                break;
                            case "3":
                                fetchCurrentInventory();
                                break;
                            case "4":
                                restoreCurrentInventory();
                                break;
                            case "5":
                                System.out.println("Manage Menu Items - Feature will be implemented in future updates.");
                                break;
                            default:
                                System.out.println("Invalid option. Please try again.");
                                break;
                        }
                    } else {
                        System.out.println("Processing your request...");
                        System.out.println("Feature will be implemented in future updates.");
                    }

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

    /**
     * Process order placement for a customer
     * @param user The current logged-in user
     * @param scanner Scanner for user input
     */
    private static void processOrderPlacement(User user, Scanner scanner) {
        System.out.println("\n=== Place New Order ===");

        // Create a new order for the customer
        Order order = customerService.placeOrder(user);
        System.out.println("Order created! Order ID: " + order.getOrderId());

        boolean addingItems = true;
        CustomerServiceImpl customerServiceImpl = (CustomerServiceImpl) customerService;

        while (addingItems) {
            // Display menu categories
            System.out.println("\nMenu Categories:");
            TreeMap<String, Map<String, String>> menuItems = menu.menuItems;
            String[] categories = menuItems.keySet().toArray(new String[0]);

            for (int i = 0; i < categories.length; i++) {
                System.out.printf("%d. %s\n", i + 1, categories[i]);
            }

            System.out.print("\nSelect category (0 to finish order): ");
            String categoryInput = scanner.nextLine().trim();

            if ("0".equals(categoryInput)) {
                addingItems = false;
                continue;
            }

            try {
                int categoryIndex = Integer.parseInt(categoryInput) - 1;
                if (categoryIndex < 0 || categoryIndex >= categories.length) {
                    System.out.println("Invalid category selection.");
                    continue;
                }

                String selectedCategory = categories[categoryIndex];
                Map<String, String> items = menu.getCategoryItems(selectedCategory);

                // Display items in the selected category
                System.out.println("\n--- " + selectedCategory + " ---");
                String[] itemNames = items.keySet().toArray(new String[0]);
                for (int i = 0; i < itemNames.length; i++) {
                    System.out.printf("%d. %s - %s\n", i + 1, itemNames[i], items.get(itemNames[i]));
                }

                System.out.print("\nSelect item (0 to go back): ");
                String itemInput = scanner.nextLine().trim();

                if ("0".equals(itemInput)) {
                    continue;
                }

                int itemIndex = Integer.parseInt(itemInput) - 1;
                if (itemIndex < 0 || itemIndex >= itemNames.length) {
                    System.out.println("Invalid item selection.");
                    continue;
                }

                String selectedItem = itemNames[itemIndex];

                System.out.print("Enter quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine().trim());

                if (quantity <= 0) {
                    System.out.println("Quantity must be greater than zero.");
                    continue;
                }

                // Add item to order
                boolean success = customerServiceImpl.addItemToOrder(order.getOrderId(), selectedCategory, selectedItem, quantity);
                if (success) {
                    System.out.println(quantity + " x " + selectedItem + " added to your order.");
                } else {
                    System.out.println("Failed to add item to order.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Check if order has items
        if (order.getItems().isEmpty()) {
            System.out.println("Order cancelled - no items added.");
            return;
        }

        // Generate and display bill
        System.out.println("\nGenerating your bill...");
        String bill = customerServiceImpl.generateBill(order.getOrderId());
        System.out.println(bill);

        System.out.println("Thank you for your order!");
    }


    /**
     * View the status of previous orders
     * @param user The current logged-in user
     * @param scanner Scanner for user input
     */
    private static void viewOrderStatus(User user, Scanner scanner) {
        System.out.println("\n=== View Order Status ===");

        // Retrieve and display user's order history
        CustomerServiceImpl customerServiceImpl = (CustomerServiceImpl) customerService;
        customerServiceImpl.viewOrderStatus(user);

        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }

    /**
     * Fetch and display the current inventory
     */
    private static void fetchCurrentInventory() {
        logger.info("Fetching current inventory data");
        System.out.println("\n=== Current Inventory ===");

        String inventoryData = adminService.fetchCurrentInventory();
        System.out.println(inventoryData);

        System.out.println("Current inventory data has been displayed.");
    }

    /**
     * Restore the current inventory from the main inventory
     */
    private static void restoreCurrentInventory() {
        logger.info("Restoring current inventory from main inventory");
        System.out.println("\n=== Restoring Inventory ===");

        boolean success = adminService.restoreCurrentInventory();

        if (success) {
            System.out.println("Inventory has been successfully restored from the main inventory.");
        } else {
            System.out.println("Failed to restore inventory. Please check the logs for details.");
        }
    }

    /**
     * Take a new order as a waiter
     * @param scanner Scanner for user input
     */
    private static void takeNewOrderAsWaiter(Scanner scanner) {
        logger.info("Taking new order as waiter");

        // Use the WaiterService to handle the order taking process
        Order order = waiterService.takeNewOrder(scanner);

        if (order != null) {
            logger.info("Order #{} successfully created by waiter", order.getOrderId());
            System.out.println("\nOrder #" + order.getOrderId() + " has been successfully created.");
        } else {
            logger.info("Order creation cancelled or failed");
            System.out.println("\nOrder creation was cancelled or failed.");
        }
    }

    /**
     * Check and display unserved tables
     */
    private static void checkUnservedTables() {
        logger.info("Checking unserved tables");
        System.out.println("\n=== Unserved Tables ===");

        // Use the WaiterService to fetch unserved tables
        java.util.List<zeta.foods.model.Table> unservedTables = waiterService.getUnservedTables();

        if (unservedTables != null && !unservedTables.isEmpty()) {
            System.out.println("Tables waiting to be served:");
            for (zeta.foods.model.Table table : unservedTables) {
                System.out.println(table.toString());
            }
            System.out.println("\nTotal unserved tables: " + unservedTables.size());
        } else {
            System.out.println("All tables are currently served.");
        }
    }

    /**
     * Handle the booking of a table for a customer
     * @param user Logged in user
     * @param scanner Scanner for input
     */
    private static void bookTableForCustomer(User user, Scanner scanner) {
        logger.info("Customer {} attempting to book a table", user.getUsername());
        System.out.println("\n=== Book Table ===");

        // Use the customer service to handle the table booking process
        // Call viewAndBookTable instead of bookTable, as this handles user input for time and table selection
        customerService.viewAndBookTable(user);
    }
}
