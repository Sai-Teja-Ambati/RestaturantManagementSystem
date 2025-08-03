package zeta.foods.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeta.foods.model.Order;
import zeta.foods.model.Table;
import zeta.foods.model.User;
import zeta.foods.service.WaiterService;
import zeta.foods.utils.DatabaseUtil;
import zeta.foods.utils.menu;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of the WaiterService interface
 */
public class WaiterServiceImpl implements WaiterService {
    private static final Logger logger = LoggerFactory.getLogger(WaiterServiceImpl.class);
    private final CustomerServiceImpl customerService = new CustomerServiceImpl();

    /**
     * Take a new order for a customer
     * @param scanner Scanner for user input
     * @return The newly created order
     */
    @Override
    public Order takeNewOrder(Scanner scanner) {
        logger.info("Taking a new order");
        System.out.println("\n=== Take New Order ===");

        System.out.println("Enter customer details or assign to a table:");
        System.out.print("Customer name: ");
        String customerName = scanner.nextLine().trim();

        // Look up customer in database by name or create temporary user
        User customer = findCustomerByName(customerName);
        if (customer == null) {
            // If customer not found, ask if this is a walk-in customer
            System.out.print("Customer not found in system. Is this a walk-in customer? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.startsWith("y")) {
                // Create temporary user for walk-in customer
                customer = new User();
                customer.setUsername(customerName);
                customer.setRole("customer");
                // Don't set ID, which will trigger the 'walk-in' customer logic in placeOrder
            } else {
                System.out.println("Order creation cancelled - customer not found.");
                return null;
            }
        }

        // Use existing customer service to handle the order placement
        Order order = customerService.placeOrder(customer);
        if (order == null) {
            System.out.println("Failed to create order. Please try again.");
            return null;
        }

        System.out.println("Order created! Order ID: " + order.getOrderId());

        // Now handle order items
        boolean addingItems = true;

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
                boolean success = customerService.addItemToOrder(order.getOrderId(), selectedCategory, selectedItem, quantity);
                if (success) {
                    System.out.println(quantity + " x " + selectedItem + " added to the order.");
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
            return null;
        }

        // Generate and display bill
        System.out.println("\nGenerating bill...");
        String bill = customerService.generateBill(order.getOrderId());
        System.out.println(bill);

        System.out.println("Order has been placed successfully!");

        // Associate the order with a table
        assignOrderToTable(order, scanner);

        return order;
    }

    /**
     * Find a customer in the database by name
     * @param customerName The name to search for
     * @return User object if found, null if not found
     */
    private User findCustomerByName(String customerName) {
        logger.info("Looking up customer by name: {}", customerName);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, username, email, role FROM users WHERE username = ? AND role = 'customer'")) {

            stmt.setString(1, customerName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User customer = new User();
                    customer.setId(rs.getLong("id"));
                    customer.setUsername(rs.getString("username"));
                    customer.setEmail(rs.getString("email"));
                    customer.setRole(rs.getString("role"));

                    logger.info("Customer found: {} (ID: {})", customerName, customer.getId());
                    return customer;
                }
            }

            logger.info("No customer found with name: {}", customerName);
            return null;

        } catch (SQLException e) {
            logger.error("Database error looking up customer: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get all tables with their current status
     * @return List of tables with occupation status
     */
    @Override
    public List<Table> getAllTables() {
        logger.info("Retrieving all tables");
        List<Table> tables = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM tables ORDER BY table_number")) {

            while (rs.next()) {
                Table table = new Table();
                table.setTableNumber(rs.getInt("table_number"));
                table.setOccupied(rs.getBoolean("is_occupied"));
                table.setServed(rs.getBoolean("is_served"));

                // Convert SQL timestamp to LocalDateTime if not null
                Timestamp startTime = rs.getTimestamp("booking_start_time");
                if (startTime != null) {
                    table.setBookingStartTime(startTime.toLocalDateTime());
                }

                Timestamp endTime = rs.getTimestamp("booking_end_time");
                if (endTime != null) {
                    table.setBookingEndTime(endTime.toLocalDateTime());
                }

                tables.add(table);
            }

            logger.info("Retrieved {} tables from database", tables.size());

        } catch (SQLException e) {
            logger.error("Error retrieving tables: {}", e.getMessage(), e);

            // If no tables exist yet, create a default set
            if (tables.isEmpty()) {
                logger.info("No tables found in database, initializing default tables");
                for (int i = 1; i <= 10; i++) {
                    createTable(i);
                    tables.add(new Table(i));
                }
            }
        }

        return tables;
    }

    /**
     * Assign an order to a table
     * @param order The order to assign
     * @param scanner Scanner for user input
     */
    private void assignOrderToTable(Order order, Scanner scanner) {
        System.out.println("\nAssign order to a table:");

        // Display available tables
        List<Table> tables = getAllTables();
        List<Table> availableTables = new ArrayList<>();

        System.out.println("\nAvailable Tables:");
        for (Table table : tables) {
            if (!table.isOccupied()) {
                availableTables.add(table);
                System.out.println(table.toString());
            }
        }

        if (availableTables.isEmpty()) {
            System.out.println("No tables are currently available. Creating a new table.");
            // Create a new table with the next number
            int nextTableNumber = tables.isEmpty() ? 1 : tables.size() + 1;
            Table newTable = createTable(nextTableNumber);
            availableTables.add(newTable);
            System.out.println(newTable.toString());
        }

        int tableNumber;
        while (true) {
            System.out.print("Enter table number: ");
            try {
                tableNumber = Integer.parseInt(scanner.nextLine().trim());

                // Check if the table exists and is available
                boolean tableValid = false;
                for (Table table : availableTables) {
                    if (table.getTableNumber() == tableNumber) {
                        tableValid = true;
                        break;
                    }
                }

                if (tableValid) {
                    break;
                } else {
                    System.out.println("Invalid or occupied table number. Please select an available table.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid table number.");
            }
        }

        // Occupy the table and link to the order
        occupyTable(tableNumber, order.getOrderId());
        System.out.println("Order #" + order.getOrderId() + " has been assigned to Table #" + tableNumber);
    }

    /**
     * Create a new table in the database
     * @param tableNumber The table number
     * @return The created table object
     */
    private Table createTable(int tableNumber) {
        logger.info("Creating new table with number: {}", tableNumber);
        Table table = new Table(tableNumber);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO tables (table_number, is_occupied, is_served) VALUES (?, ?, ?) " +
                             "ON CONFLICT (table_number) DO NOTHING")) {

            stmt.setInt(1, tableNumber);
            stmt.setBoolean(2, false);
            stmt.setBoolean(3, false);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error creating table: {}", e.getMessage(), e);
        }

        return table;
    }

    /**
     * Occupy a table and associate it with an order
     * @param tableNumber The table number to occupy
     * @param orderId The order ID to associate with the table
     */
    private void occupyTable(int tableNumber, String orderId) {
        logger.info("Occupying table #{} for order ID: {}", tableNumber, orderId);

        try (Connection conn = DatabaseUtil.getConnection()) {
            // First update the table status
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE tables SET is_occupied = true, is_served = false, booking_start_time = NOW() " +
                            "WHERE table_number = ?")) {

                stmt.setInt(1, tableNumber);
                stmt.executeUpdate();
            }

            // Then get the table ID
            int tableId;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id FROM tables WHERE table_number = ?")) {

                stmt.setInt(1, tableNumber);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    tableId = rs.getInt("id");

                    // Associate table with order
                    try (PreparedStatement linkStmt = conn.prepareStatement(
                            "INSERT INTO order_tables (order_id, table_id) VALUES (?, ?)")) {

                        linkStmt.setString(1, orderId);  // Changed from setLong to setString
                        linkStmt.setInt(2, tableId);
                        linkStmt.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Error occupying table: {}", e.getMessage(), e);
        }
    }

    /**
     * Get all tables that are occupied but not yet served
     * @return List of unserved tables
     */
    @Override
    public List<Table> getUnservedTables() {
        logger.info("Retrieving unserved tables");
        List<Table> unservedTables = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM tables WHERE is_occupied = true AND is_served = false ORDER BY table_number")) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Table table = new Table();
                    table.setTableNumber(rs.getInt("table_number"));
                    table.setOccupied(rs.getBoolean("is_occupied"));
                    table.setServed(rs.getBoolean("is_served"));

                    // Convert SQL timestamp to LocalDateTime if not null
                    Timestamp startTime = rs.getTimestamp("booking_start_time");
                    if (startTime != null) {
                        table.setBookingStartTime(startTime.toLocalDateTime());
                    }

                    Timestamp endTime = rs.getTimestamp("booking_end_time");
                    if (endTime != null) {
                        table.setBookingEndTime(endTime.toLocalDateTime());
                    }

                    unservedTables.add(table);
                }
            }

            logger.info("Retrieved {} unserved tables from database", unservedTables.size());

        } catch (SQLException e) {
            logger.error("Error retrieving unserved tables: {}", e.getMessage(), e);
        }

        return unservedTables;
    }
}
