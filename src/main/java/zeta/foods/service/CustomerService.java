package zeta.foods.service;

import zeta.foods.model.Order;
import zeta.foods.model.User;

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
}
