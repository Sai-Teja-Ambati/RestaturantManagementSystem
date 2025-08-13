package zeta.foods.service;

import zeta.foods.model.Order;
import zeta.foods.model.Table;
import zeta.foods.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Service interface for customer-specific operations
 */
public interface CustomerService {

    /**
     * Place a new order for a customer
     * @param user The customer placing the order
     * @return The created order
     */
    Order placeOrder(User user);

    /**
     * Get all orders for a customer
     * @param customerId Customer ID
     * @return List of customer's orders
     */
    List<Order> getCustomerOrders(Long customerId);

    /**
     * Get order status by order ID
     * @param orderId Order ID
     * @return The order if found, null otherwise
     */
    Order getOrderStatus(String orderId);

    /**
     * Get a specified number of previous orders for a customer, sorted by most recent first
     *
     * @param customerId The customer's ID
     * @param limit      Maximum number of orders to retrieve (default is 1 for latest order)
     * @return List of the customer's orders in descending date order
     */
    List<Order> getPreviousOrders(Long customerId, int limit);

    /**
     * Format a single order for display
     *
     * @param order The order to format
     * @return Formatted order summary as string
     */
    String formatOrderSummary(Order order);

    /**
     * Display the restaurant menu in a two-page format
     * @param scanner Scanner for user input
     */
    void displayRestaurantMenu(Scanner scanner);

    /**
     * View order status for a user
     * @param user The user viewing their orders
     */
    void viewOrderStatus(User user);

    /**
     * Get available tables at a specific timestamp
     *
     * @param requestedDateTime The timestamp when the customer wants to book a table
     * @return List of available tables at that time
     */
    List<Table> getAvailableTablesAtTime(LocalDateTime requestedDateTime);

    /**
     * Book a table for a customer at a specific time period
     *
     * @param user The user booking the table
     * @param tableNumber The table number to book
     * @param startTime The start time of the booking
     * @param endTime The end time of the booking
     * @return true if booking was successful, false otherwise
     */
    boolean bookTable(User user, int tableNumber, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Display available tables at a specific time for a customer to choose from
     *
     * @param user The user viewing available tables
     */
    void viewAndBookTable(User user);
}
