package org.restaurant.service;

import org.restaurant.dto.request.CreateOrderRequest;
import org.restaurant.dto.request.UpdateOrderStatusRequest;
import org.restaurant.dto.response.OrderResponse;
import org.restaurant.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for order operations
 */
public interface OrderService {

    /**
     * Create a new order
     * @param request Order creation request
     * @return Order response
     */
    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * Get all orders
     * @return List of order responses
     */
    List<OrderResponse> getAllOrders();

    /**
     * Get order by ID
     * @param orderId Order ID
     * @return Order response
     */
    OrderResponse getOrderById(Long orderId);

    /**
     * Update order status
     * @param orderId Order ID
     * @param request Status update request
     * @return Updated order response
     */
    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    /**
     * Get orders by table number
     * @param tableNumber Table number
     * @return List of order responses
     */
    List<OrderResponse> getOrdersByTableNumber(Integer tableNumber);

    /**
     * Get orders by status
     * @param status Order status
     * @return List of order responses
     */
    List<OrderResponse> getOrdersByStatus(OrderStatus status);

    /**
     * Get orders by waiter ID
     * @param waiterId Waiter ID
     * @return List of order responses
     */
    List<OrderResponse> getOrdersByWaiterId(Long waiterId);

    /**
     * Get current user's orders
     * @return List of order responses
     */
    List<OrderResponse> getCurrentUserOrders();

    /**
     * Get orders by date
     * @param date Date
     * @return List of order responses
     */
    List<OrderResponse> getOrdersByDate(LocalDateTime date);

    /**
     * Get kitchen orders
     * @return List of order responses
     */
    List<OrderResponse> getKitchenOrders();

    /**
     * Move order to kitchen
     * @param orderId Order ID
     * @return Updated order response
     */
    OrderResponse moveToKitchen(Long orderId);

    /**
     * Mark order as served
     * @param orderId Order ID
     * @return Updated order response
     */
    OrderResponse markAsServed(Long orderId);

    /**
     * Cancel order
     * @param orderId Order ID
     */
    void cancelOrder(Long orderId);
}