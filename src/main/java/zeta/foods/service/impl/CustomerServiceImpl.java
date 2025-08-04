package zeta.foods.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeta.foods.model.Order;
import zeta.foods.model.OrderItem;
import zeta.foods.model.OrderStatus;
import zeta.foods.model.User;
import zeta.foods.model.Recipe;
import zeta.foods.service.CustomerService;
import zeta.foods.utils.CurrentInventory;
import zeta.foods.utils.DatabaseUtil;
import zeta.foods.utils.menu;
import zeta.foods.utils.recipes;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Implementation of CustomerService for handling customer orders
 */
public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private static final Map<String, Order> orders = new HashMap<>();
    private static final Map<Long, List<Order>> customerOrders = new HashMap<>();

    @Override
    public Order placeOrder(User user) {
        Order order = new Order();

        // Handle case where user ID might be null (for temporary users created by waiters)
        if (user.getId() != null) {
            order.setCustomerId(user.getId());
        } else {
            // For walk-in customers or when waiter creates an order without a registered user
            order.setCustomerId(0L);  // Use 0 to indicate a walk-in customer or temporary user
        }

        order.setCustomerName(user.getUsername());

        boolean dbSaveSuccessful = false;
        // Save order to database (initially empty)
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Use transaction to ensure consistency
            conn.setAutoCommit(false);

            String sql = "INSERT INTO orders (order_id, customer_id, items, bill_subtotal, order_status) VALUES (?, ?, ?::jsonb, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, order.getOrderId());

                // If user ID is null, use 0 for customer_id in database
                if (user.getId() != null) {
                    stmt.setLong(2, user.getId());
                } else {
                    stmt.setLong(2, 0L);
                }

                stmt.setString(3, "[]"); // Empty JSON array for items initially
                stmt.setDouble(4, 0.0); // Initial subtotal is 0
                stmt.setString(5, order.getStatus().toString());

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Verify the order was actually saved
                    try (PreparedStatement verifyStmt = conn.prepareStatement("SELECT COUNT(*) FROM orders WHERE order_id = ?")) {
                        verifyStmt.setString(1, order.getOrderId());
                        try (ResultSet rs = verifyStmt.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                // Order exists in the database
                                conn.commit();
                                dbSaveSuccessful = true;
                                logger.info("Order {} successfully saved to database and verified", order.getOrderId());
                            } else {
                                conn.rollback();
                                logger.error("Order {} insert appeared successful but verification failed", order.getOrderId());
                            }
                        }
                    }
                } else {
                    conn.rollback();
                    logger.error("Failed to save order {} to database (no rows affected)", order.getOrderId());
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while saving order {}: {}", order.getOrderId(), e.getMessage(), e);
        }

        // Only store the order in memory if database save was successful
        if (dbSaveSuccessful) {
            // Store order in memory cache
            if (user.getId() != null) {
                if (!customerOrders.containsKey(user.getId())) {
                    customerOrders.put(user.getId(), new ArrayList<>());
                }
                customerOrders.get(user.getId()).add(order);
            }
            orders.put(order.getOrderId(), order);

            logger.info("Created new order with ID: {} for customer: {}", order.getOrderId(), user.getUsername());
            return order;
        } else {
            logger.error("Order creation failed due to database error. Order ID: {}", order.getOrderId());
            return null;
        }
    }

    @Override
    public List<Order> getCustomerOrders(Long customerId) {
        // Try to get orders from memory first
        List<Order> cachedOrders = customerOrders.getOrDefault(customerId, new ArrayList<>());

        // If no cached orders, try to load from database
        if (cachedOrders.isEmpty()) {
            return loadOrdersFromDatabase(customerId);
        }

        return cachedOrders;
    }

    private List<Order> loadOrdersFromDatabase(Long customerId) {
        List<Order> result = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_timestamp DESC";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, customerId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Order order = new Order();
                        String orderId = rs.getString("order_id");
                        order.setOrderId(orderId);
                        order.setCustomerId(customerId);
                        order.setStatus(OrderStatus.valueOf(rs.getString("order_status")));
                        order.setOrderTime(rs.getTimestamp("order_timestamp").toLocalDateTime());

                        // Parse items from JSON
                        String itemsJson = rs.getString("items");
                        parseOrderItems(order, itemsJson);

                        result.add(order);

                        // Add to cache
                        orders.put(orderId, order);
                    }
                }
            }

            // Update the cache
            if (!result.isEmpty()) {
                customerOrders.put(customerId, result);
            }

        } catch (SQLException e) {
            logger.error("Database error while loading orders: {}", e.getMessage(), e);
        }

        return result;
    }

    private void parseOrderItems(Order order, String itemsJson) {
        try {
            JSONArray itemsArray = new JSONArray(itemsJson);
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemObj = itemsArray.getJSONObject(i);
                String category = itemObj.getString("category");
                String itemName = itemObj.getString("itemName");
                double price = itemObj.getDouble("price");
                int quantity = itemObj.getInt("quantity");

                OrderItem item = new OrderItem(category, itemName, quantity, price);
                order.addItem(item);
            }
        } catch (Exception e) {
            logger.error("Error parsing order items JSON: {}", e.getMessage(), e);
        }
    }

    @Override
    public Order getOrderStatus(String orderId) {
        // Try to get from cache first
        Order order = orders.get(orderId);

        // If not in cache, try to load from database
        if (order == null) {
            try (Connection conn = DatabaseUtil.getConnection()) {
                String sql = "SELECT * FROM orders WHERE order_id = ?";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, orderId);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            order = new Order();
                            order.setOrderId(orderId);
                            order.setCustomerId(rs.getLong("customer_id"));
                            order.setStatus(OrderStatus.valueOf(rs.getString("order_status")));
                            order.setOrderTime(rs.getTimestamp("order_timestamp").toLocalDateTime());

                            // Parse items from JSON
                            String itemsJson = rs.getString("items");
                            parseOrderItems(order, itemsJson);

                            // Add to cache
                            orders.put(orderId, order);
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error("Database error while getting order status: {}", e.getMessage(), e);
            }
        }

        return order;
    }

    /**
     * Add an item to an existing order
     *
     * @param orderId   The order ID
     * @param category   Menu category
     * @param itemName  Item name
     * @param quantity  Quantity
     * @return true if successful, false otherwise
     */
    public boolean addItemToOrder(String orderId, String category, String itemName, int quantity) {
        Order order = orders.get(orderId);
        if (order == null) {
            logger.warn("Order not found: {}", orderId);
            return false;
        }

        // Get price from menu
        String priceStr = menu.getItemPrice(category, itemName);
        if (priceStr == null) {
            logger.warn("Item not found in menu: {} in category {}", itemName, category);
            return false;
        }

        // Parse price (remove "Rs." prefix)
        double price = menu.getPriceValue(priceStr);

        // Check if we have the necessary ingredients for this item
        OrderItem tempItem = new OrderItem(category, itemName, quantity, price);
        List<OrderItem> itemsToCheck = new ArrayList<>();
        itemsToCheck.add(tempItem);

        // Check ingredient availability before adding to order
        if (!CurrentInventory.checkIngredientsAvailability(itemsToCheck)) {
            logger.warn("Cannot add {} to order: insufficient ingredients", itemName);
            logger.info("Sorry, we don't have enough ingredients to prepare " + itemName + " at this time.");
            return false;
        }

        // Add item to order
        OrderItem item = new OrderItem(category, itemName, quantity, price);
        order.addItem(item);

        // Update the order in the database
        try (Connection conn = DatabaseUtil.getConnection()) {
            // First, get all existing items for this order
            String itemsJson = createItemsJson(order);
            logger.debug("Order subtotal: {}", order.getTotalAmount());

            // Check if order exists in database
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM orders WHERE order_id = ?")) {
                checkStmt.setString(1, orderId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        logger.error("Order {} does not exist in database", orderId);
                        return false;
                    }
                }
            }

            String sql = "UPDATE orders SET items = ?::jsonb, bill_subtotal = ? WHERE order_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, itemsJson);
                stmt.setDouble(2, order.getTotalAmount());
                stmt.setString(3, orderId);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    logger.error("Failed to update order {} in database (No rows affected)", orderId);
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while updating order {}: {}", orderId, e.getMessage(), e);
            return false;
        }

        // Deduct ingredients from current inventory
        CurrentInventory.useIngredientsForOrder(itemsToCheck);
        logger.info("Added item to order {}: {} x{} ({})",
                orderId, itemName, quantity, priceStr);
        return true;
    }

    private String createItemsJson(Order order) {
        JSONArray itemsArray = new JSONArray();

        for (OrderItem item : order.getItems()) {
            JSONObject itemObj = new JSONObject();
            itemObj.put("category", item.getCategory());
            itemObj.put("itemName", item.getItemName());
            itemObj.put("price", item.getPrice());
            itemObj.put("quantity", item.getQuantity());
            itemsArray.put(itemObj);
        }

        return itemsArray.toString();
    }

    /**
     * Update order status
     *
     * @param orderId Order ID
     * @param status  New status
     * @return true if successful, false otherwise
     */
    public boolean updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orders.get(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(status);

        // Update status in database
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status.toString());
                stmt.setString(2, orderId);

                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (SQLException e) {
            logger.error("Database error while updating order status: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get a specified number of previous orders for a customer, sorted by most recent first
     *
     * @param customerId The customer's ID
     * @param limit      Maximum number of orders to retrieve (default is 1 for latest order)
     * @return List of the customer's orders in descending date order
     */
    public List<Order> getPreviousOrders(Long customerId, int limit) {
        // Check if we have any orders in memory first
        List<Order> customerOrderList = customerOrders.getOrDefault(customerId, new ArrayList<>());

        // If no orders in memory, try to load from database
        if (customerOrderList.isEmpty()) {
            logger.info("No orders found in memory for customer ID: {}, loading from database...", customerId);
            customerOrderList = loadOrdersFromDatabase(customerId);
        }

        // If still empty after trying to load from DB, return empty list
        if (customerOrderList.isEmpty()) {
            logger.info("No orders found in database for customer ID: {}", customerId);
            return new ArrayList<>();
        }

        // Sort orders by order time, most recent first
        return customerOrderList.stream()
                .sorted(Comparator.comparing(Order::getOrderTime).reversed())
                .limit(limit > 0 ? limit : 1) // Apply limit, with default of 1 if invalid limit
                .collect(Collectors.toList());
    }

    /**
     * Format a single order for display
     *
     * @param order The order to format
     * @return Formatted order summary as string
     */
    public String formatOrderSummary(Order order) {
        if (order == null) {
            return "No order information available.";
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        StringBuilder summary = new StringBuilder();
        summary.append("\n=================================\n");
        summary.append("ORDER SUMMARY\n");
        summary.append("=================================\n");
        summary.append("Order ID: ").append(order.getOrderId()).append("\n");
        summary.append("Date: ").append(order.getOrderTime().format(dateFormatter)).append("\n");
        summary.append("Time: ").append(order.getOrderTime().format(timeFormatter)).append("\n");
        summary.append("Status: ").append(order.getStatus()).append("\n");
        summary.append("---------------------------------\n");

        // List items
        summary.append("ITEMS ORDERED:\n");
        for (OrderItem item : order.getItems()) {
            summary.append(String.format("• %d x %s (Rs.%.2f)\n",
                    item.getQuantity(), item.getItemName(), item.getPrice()));
        }

        summary.append("---------------------------------\n");
        summary.append(String.format("Subtotal: Rs.%.2f\n", order.getTotalAmount()));

        // Calculate additional charges
        double serviceCharge = order.getTotalAmount() * 0.02;
        double taxCharge = order.getTotalAmount() > 150 ? 25.0 : 0.0;
        double grandTotal = order.getTotalAmount() + serviceCharge + taxCharge;

        summary.append(String.format("Service Charge (2%%): Rs.%.2f\n", serviceCharge));
        summary.append(String.format("CGST & SGST: Rs.%.2f\n", taxCharge));
        summary.append(String.format("Grand Total: Rs.%.2f\n", grandTotal));
        summary.append("=================================\n");

        return summary.toString();
    }

    /**
     * Format multiple orders for display
     *
     * @param orders List of orders to format
     * @return Formatted list of order summaries as string
     */
    public String formatOrdersList(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return "No previous orders found.";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        StringBuilder result = new StringBuilder();
        result.append("\n=================================\n");
        result.append("YOUR ORDER HISTORY\n");
        result.append("=================================\n");

        int count = 1;
        for (Order order : orders) {
            result.append(String.format("%d. Order #%s - %s\n",
                    count++,
                    order.getOrderId(),
                    order.getOrderTime().format(formatter)));

            result.append(String.format("   Status: %s, Total: Rs.%.2f\n",
                    order.getStatus(),
                    order.getTotalAmount()));

            // Show item count
            result.append(String.format("   Items: %d\n", order.getItems().size()));
        }

        result.append("=================================\n");
        return result.toString();
    }

    /**
     * View order status for a customer with option to specify number of orders to display
     *
     * @param user The user viewing their order history
     */
    public void viewOrderStatus(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Your Order History ===");

        // Ask user how many orders they want to view
        System.out.print("How many recent orders would you like to view? (default: 1): ");
        String input = scanner.nextLine().trim();

        // Parse input, default to 1 if empty or invalid
        int limit = 1;
        if (!input.isEmpty()) {
            try {
                limit = Integer.parseInt(input);
                if (limit <= 0) {
                    limit = 1;
                    System.out.println("Invalid number. Showing the most recent order.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Showing the most recent order.");
            }
        }

        // Get previous orders for the customer
        List<Order> previousOrders = getPreviousOrders(user.getId(), limit);

        if (previousOrders.isEmpty()) {
            System.out.println("You don't have any orders yet.");
            return;
        }

        // Display each order
        System.out.printf("Showing your %d most recent order(s):\n\n", previousOrders.size());
        for (int i = 0; i < previousOrders.size(); i++) {
            if (i > 0) {
                System.out.println("\n" + "-".repeat(40) + "\n");
            }
            Order order = previousOrders.get(i);
            String formattedOrder = formatOrderSummary(order);
            System.out.println(formattedOrder);
        }
    }

    /**
     * Display the restaurant menu in a two-page format
     * @param scanner Scanner for user input
     */
    public void displayRestaurantMenu(Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("          RESTAURANT MENU - PAGE 1      ");
        System.out.println("========================================");

        TreeMap<String, Map<String, String>> menuItems = menu.menuItems;
        String[] categories = menuItems.keySet().toArray(new String[0]);

        // First page: first half of categories
        int midpoint = categories.length / 2;
        for (int i = 0; i < midpoint; i++) {
            displayMenuCategory(categories[i], menuItems.get(categories[i]));
        }

        System.out.println("\nPress Enter to see the next page...");
        scanner.nextLine();

        // Second page: second half of categories
        System.out.println("\n========================================");
        System.out.println("          RESTAURANT MENU - PAGE 2      ");
        System.out.println("========================================");

        for (int i = midpoint; i < categories.length; i++) {
            displayMenuCategory(categories[i], menuItems.get(categories[i]));
        }

        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }

    /**
     * Display a single menu category
     * @param categoryName Name of the category
     * @param items Map of items and their prices
     */
    private void displayMenuCategory(String categoryName, Map<String, String> items) {
        System.out.println("\n--- " + categoryName + " ---");
        for (Map.Entry<String, String> item : items.entrySet()) {
            System.out.printf("%-30s %10s\n", item.getKey(), item.getValue());
        }
    }

    /**
     * Generate bill for an order
     *
     * @param orderId The order ID
     * @return Formatted bill as String
     */
    public String generateBill(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return "Order not found";
        }

        // Calculate additional charges
        double subtotal = order.getTotalAmount();
        double serviceCharge = subtotal * 0.02;
        double gst = subtotal * 0.05;
        double finalAmount = subtotal + serviceCharge + gst;

        // Update order status and payment info in database
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "UPDATE orders SET " +
                    "bill_subtotal = ?, " +
                    "service_charge = ?, " +
                    "cgst_sgst = ?, " +
                    "bill_total = ?, " +
                    "payment_status = ?, " +
                    "order_status = ? " +
                    "WHERE order_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, subtotal);
                stmt.setDouble(2, serviceCharge);
                stmt.setDouble(3, gst);
                stmt.setDouble(4, finalAmount);
                stmt.setString(5, "COMPLETED");
                stmt.setString(6, "COMPLETED");
                stmt.setString(7, orderId);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    logger.error("Failed to update bill information in database for order {}", orderId);
                } else {
                    logger.info("Bill information saved to database for order {}", orderId);
                    // Update status in memory too
                    order.setStatus(OrderStatus.COMPLETED);
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while generating bill: {}", e.getMessage(), e);
        }

        // Build bill content
        StringBuilder bill = new StringBuilder();
        bill.append("\n==========================================\n");
        bill.append("             RESTAURANT BILL              \n");
        bill.append("==========================================\n");
        bill.append("Order ID: ").append(order.getOrderId()).append("\n");
        bill.append("Customer: ").append(order.getCustomerName()).append("\n");
        bill.append("Date: ").append(order.getOrderTime().toLocalDate()).append("\n");
        bill.append("Time: ").append(order.getOrderTime().toLocalTime()).append("\n");
        bill.append("------------------------------------------\n");
        bill.append(String.format("%-25s %-8s %-10s %-10s\n", "Item", "Qty", "Price", "Subtotal"));
        bill.append("------------------------------------------\n");

        for (OrderItem item : order.getItems()) {
            bill.append(String.format("%-25s %-8d Rs.%-8.2f Rs.%.2f\n",
                    item.getItemName(), item.getQuantity(), item.getPrice(), item.getSubtotal()));
        }

        bill.append("------------------------------------------\n");
        bill.append(String.format("%-25s %19s Rs.%.2f\n", "Total Amount", "", subtotal));
        bill.append(String.format("%-25s %19s Rs.%.2f\n", "GST (5%)", "", gst));
        bill.append(String.format("%-25s %19s Rs.%.2f\n", "Service Charge (2%)", "", serviceCharge));

        bill.append("------------------------------------------\n");
        bill.append(String.format("%-25s %19s Rs.%.2f\n", "Grand Total", "", finalAmount));
        bill.append("==========================================\n");
        bill.append("          Thank You! Visit Again!         \n");
        bill.append("==========================================\n");

        return bill.toString();
    }

    /**
     * Book a table for a customer
     * @param user The customer booking the table
     * @param scanner Scanner for user input
     * @return true if booking was successful, false otherwise
     */
    @Override
    public boolean bookTable(User user, Scanner scanner) {
        logger.info("Customer {} attempting to book a table", user.getUsername());
        System.out.println("\n=== Table Booking ===");

        // Get available tables from database
        List<zeta.foods.model.Table> availableTables = getAvailableTables();

        if (availableTables.isEmpty()) {
            System.out.println("Sorry, there are no tables available at the moment.");
            return false;
        }

        // Display available tables
        System.out.println("\nAvailable Tables:");
        for (zeta.foods.model.Table table : availableTables) {
            System.out.println(table.toString());
        }

        // Ask customer to select a table
        int tableNumber;
        while (true) {
            System.out.print("\nEnter table number to book (0 to cancel): ");
            try {
                tableNumber = Integer.parseInt(scanner.nextLine().trim());

                if (tableNumber == 0) {
                    System.out.println("Booking cancelled.");
                    return false;
                }

                // Validate table selection
                boolean validTable = false;
                for (zeta.foods.model.Table table : availableTables) {
                    if (table.getTableNumber() == tableNumber) {
                        validTable = true;
                        break;
                    }
                }

                if (validTable) {
                    break;
                } else {
                    System.out.println("Invalid table number. Please select from the available tables.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Book the table (update database)
        boolean success = reserveTable(tableNumber, user.getId());

        if (success) {
            System.out.println("\nTable #" + tableNumber + " has been successfully booked for you.");
            System.out.println("Please arrive within 30 minutes of your reservation time.");
            return true;
        } else {
            System.out.println("\nSorry, there was an error booking your table. Please try again later.");
            return false;
        }
    }

    /**
     * Get list of available (unoccupied) tables
     * @return List of available tables
     */
    private List<zeta.foods.model.Table> getAvailableTables() {
        List<zeta.foods.model.Table> availableTables = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM tables WHERE is_occupied = FALSE ORDER BY table_number")) {

            while (rs.next()) {
                zeta.foods.model.Table table = new zeta.foods.model.Table();
                table.setTableNumber(rs.getInt("table_number"));
                table.setOccupied(false);
                table.setServed(false);

                availableTables.add(table);
            }

            logger.info("Retrieved {} available tables from database", availableTables.size());

        } catch (SQLException e) {
            logger.error("Error retrieving available tables: {}", e.getMessage(), e);
        }

        return availableTables;
    }

    /**
     * Reserve a table for a customer
     * @param tableNumber The table number to reserve
     * @param customerId The customer ID making the reservation
     * @return true if reservation was successful, false otherwise
     */
    private boolean reserveTable(int tableNumber, Long customerId) {
        logger.info("Reserving table #{} for customer ID: {}", tableNumber, customerId);

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);

            // First check if the table is still available
            try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT * FROM tables WHERE table_number = ? AND is_occupied = FALSE")) {

                checkStmt.setInt(1, tableNumber);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    logger.warn("Table #{} is no longer available", tableNumber);
                    conn.rollback();
                    return false;
                }

                int tableId = rs.getInt("id");

                // Update table status to occupied
                try (PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE tables SET is_occupied = TRUE, booking_start_time = NOW() WHERE id = ?")) {

                    updateStmt.setInt(1, tableId);
                    updateStmt.executeUpdate();
                }

                // Create reservation record
                try (PreparedStatement reserveStmt = conn.prepareStatement(
                        "INSERT INTO table_reservations (table_id, customer_id, reservation_time, status) " +
                                "VALUES (?, ?, NOW(), 'active')")) {

                    reserveStmt.setInt(1, tableId);
                    reserveStmt.setLong(2, customerId);
                    reserveStmt.executeUpdate();
                }

                // Commit transaction
                conn.commit();
                logger.info("Table #{} successfully reserved for customer ID: {}", tableNumber, customerId);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error reserving table: {}", e.getMessage(), e);
            return false;
        }
    }
}
