package org.restaurant.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restaurant.dto.request.CreateOrderRequest;
import org.restaurant.dto.request.UpdateOrderStatusRequest;
import org.restaurant.dto.response.OrderResponse;
import org.restaurant.dto.response.SuccessResponse;
import org.restaurant.enums.OrderStatus;
import org.restaurant.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * Place a new order
     * POST /orders
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        logger.info("Creating new order for table: {}", request.getTableNumber());
        OrderResponse order = orderService.createOrder(request);
        logger.info("Order created successfully with ID: {}", order.getId());
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    /**
     * Get all orders
     * GET /orders
     * Required: ADMIN, MANAGER, WAITER, or CHEF role
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        logger.info("Fetching all orders");
        List<OrderResponse> orders = orderService.getAllOrders();
        logger.info("Retrieved {} orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order by ID
     * GET /orders/{id}
     * Required: ADMIN, MANAGER, WAITER, or CHEF role
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        logger.info("Fetching order with ID: {}", id);
        OrderResponse order = orderService.getOrderById(id);
        logger.info("Order details: {}", order);
        return ResponseEntity.ok(order);
    }

    /**
     * Update order status
     * PUT /orders/{id}
     * Required: ADMIN, MANAGER, WAITER, or CHEF role
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateOrderStatusRequest request) {
        logger.info("Updating order status for ID: {} to {}", id, request.getStatus());
        OrderResponse updatedOrder = orderService.updateOrderStatus(id, request);
        logger.info("Order status updated successfully for ID: {}", id);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Get orders by table number
     * GET /orders/table/{tableNumber}
     * Required: ADMIN, MANAGER, WAITER, or CHEF role
     */
    @GetMapping("/table/{tableNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<List<OrderResponse>> getOrdersByTableNumber(@PathVariable Integer tableNumber) {
        logger.info("Fetching orders for table number: {}", tableNumber);
        List<OrderResponse> orders = orderService.getOrdersByTableNumber(tableNumber);
        logger.info("Retrieved {} orders for table number {}", orders.size(), tableNumber);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by status
     * GET /orders/status/{status}
     * Required: ADMIN, MANAGER, WAITER, or CHEF role
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        logger.info("Fetching orders with status: {}", status);
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        logger.info("Retrieved {} orders with status {}", orders.size(), status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by waiter ID
     * GET /orders/waiter/{waiterId}
     * Required: ADMIN, MANAGER, or the specific WAITER
     */
    @GetMapping("/waiter/{waiterId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or (hasRole('WAITER') and @authenticationService.getCurrentUserId() == #waiterId)")
    public ResponseEntity<List<OrderResponse>> getOrdersByWaiterId(@PathVariable Long waiterId) {
        logger.info("Fetching orders for waiter ID: {}", waiterId);
        List<OrderResponse> orders = orderService.getOrdersByWaiterId(waiterId);
        logger.info("Retrieved {} orders for waiter ID {}", orders.size(), waiterId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get current user's orders (for waiters)
     * GET /orders/my-orders
     * Required: Any authenticated user
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<List<OrderResponse>> getCurrentUserOrders() {
        logger.info("Fetching current user's orders");
        List<OrderResponse> orders = orderService.getCurrentUserOrders();
        logger.info("Retrieved {} orders for current user", orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by date
     * GET /orders/date/{date}
     * Required: ADMIN, MANAGER, WAITER, or CHEF role
     */
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<List<OrderResponse>> getOrdersByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        logger.info("Fetching orders for date: {}", date);
        List<OrderResponse> orders = orderService.getOrdersByDate(date);
        logger.info("Retrieved {} orders for date {}", orders.size(), date);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get kitchen orders (for chef view)
     * GET /orders/kitchen
     * Required: ADMIN, MANAGER, WAITER, or CHEF role
     */
    @GetMapping("/kitchen")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<List<OrderResponse>> getKitchenOrders() {
        logger.info("Fetching kitchen orders");
        List<OrderResponse> orders = orderService.getKitchenOrders();
        logger.info("Retrieved {} kitchen orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * Move order to kitchen
     * PUT /orders/{id}/kitchen
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @PutMapping("/{id}/kitchen")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<OrderResponse> moveOrderToKitchen(@PathVariable Long id) {
        logger.info("Moving order ID: {} to kitchen", id);
        OrderResponse order = orderService.moveToKitchen(id);
        logger.info("Order ID: {} moved to kitchen successfully", id);
        return ResponseEntity.ok(order);
    }

    /**
     * Mark order as served
     * PUT /orders/{id}/served
     * Required: ADMIN, MANAGER, WAITER, or CHEF role
     */
    @PutMapping("/{id}/served")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<OrderResponse> markOrderAsServed(@PathVariable Long id) {
        logger.info("Marking order ID: {} as served", id);
        OrderResponse order = orderService.markAsServed(id);
        logger.info("Order ID: {} marked as served successfully", id);
        return ResponseEntity.ok(order);
    }

    /**
     * Cancel order
     * DELETE /orders/{id}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<SuccessResponse> cancelOrder(@PathVariable Long id) {
        logger.info("Cancelling order ID: {}", id);
        orderService.cancelOrder(id);
        logger.info("Order ID: {} cancelled successfully", id);
        return ResponseEntity.ok(new SuccessResponse("Order cancelled successfully"));
    }

    /**
     * Get today's orders
     * GET /orders/today
     * Required: ADMIN, MANAGER, WAITER, or CHEF role
     */
    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<List<OrderResponse>> getTodaysOrders() {
        logger.info("Fetching today's orders");
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<OrderResponse> orders = orderService.getOrdersByDate(today);
        logger.info("Retrieved {} orders for today", orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * Get placed orders (PLACED status)
     * GET /orders/placed
     * Required: ADMIN, MANAGER, WAITER, or CHEF role
     */
    @GetMapping("/placed")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<List<OrderResponse>> getPlacedOrders() {
        logger.info("Fetching placed orders");
        List<OrderResponse> orders = orderService.getOrdersByStatus(OrderStatus.PLACED);
        logger.info("Retrieved {} placed orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * Get in-kitchen orders (IN_KITCHEN status)
     * GET /orders/in-kitchen
     * Required: ADMIN, MANAGER, WAITER, or CHEF role
     */
    @GetMapping("/in-kitchen")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<List<OrderResponse>> getInKitchenOrders() {
        logger.info("Fetching in-kitchen orders");
        List<OrderResponse> orders = orderService.getOrdersByStatus(OrderStatus.IN_KITCHEN);
        logger.info("Retrieved {} in-kitchen orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * Get served orders (SERVED status)
     * GET /orders/served
     * Required: ADMIN, MANAGER, WAITER, or CHEF role
     */
    @GetMapping("/served")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<List<OrderResponse>> getServedOrders() {
        logger.info("Fetching served orders");
        List<OrderResponse> orders = orderService.getOrdersByStatus(OrderStatus.SERVED);
        logger.info("Retrieved {} served orders", orders.size());
        return ResponseEntity.ok(orders);
    }
}