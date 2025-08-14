package com.restaurant.controller;

import com.restaurant.entity.Order;
import com.restaurant.enums.OrderStatus;
import com.restaurant.service.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chef")
@CrossOrigin(origins = "*")
@Slf4j
public class ChefController {

    @Autowired
    private OrderService orderService;

    /**
     * Get all orders for kitchen view
     */
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            log.info("Chef fetching all orders");
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching orders for chef: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Get orders that are placed (new orders for kitchen)
     */
    @GetMapping("/orders/placed")
    public ResponseEntity<List<Order>> getPlacedOrders() {
        try {
            log.info("Chef fetching placed orders");
            List<Order> orders = orderService.getOrdersByStatus(OrderStatus.PLACED);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching placed orders: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Get orders that are in kitchen (currently being prepared)
     */
    @GetMapping("/orders/in-kitchen")
    public ResponseEntity<List<Order>> getInKitchenOrders() {
        try {
            log.info("Chef fetching in-kitchen orders");
            List<Order> orders = orderService.getOrdersByStatus(OrderStatus.IN_KITCHEN);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching in-kitchen orders: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Update order status - Chef's primary function
     * Chef can move orders through the kitchen workflow:
     * PLACED -> IN_KITCHEN -> SERVED
     */
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable String orderId, 
            @Valid @RequestBody Map<String, String> request) {
        try {
            log.info("Chef updating order status for order: {}", orderId);
            
            String statusStr = request.get("status");
            if (statusStr == null || statusStr.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }

            // Validate that the status is a valid OrderStatus
            OrderStatus newStatus;
            try {
                newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid status. Valid statuses are: PLACED, IN_KITCHEN, SERVED, CANCELLED"
                ));
            }

            // Chef can only update to kitchen-related statuses
            if (newStatus != OrderStatus.PLACED && 
                newStatus != OrderStatus.IN_KITCHEN && 
                newStatus != OrderStatus.SERVED) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Chef can only update status to: PLACED, IN_KITCHEN, or SERVED"
                ));
            }

            Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
            
            log.info("Order status updated successfully by chef: {} -> {}", orderId, newStatus);
            return ResponseEntity.ok(Map.of(
                    "message", "Order status updated successfully",
                    "orderId", updatedOrder.getOrderId(),
                    "previousStatus", request.get("previousStatus"),
                    "newStatus", updatedOrder.getOrderStatus(),
                    "tableNumber", updatedOrder.getTableNumber(),
                    "updatedBy", "CHEF"
            ));
            
        } catch (RuntimeException e) {
            log.error("Error updating order status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating order status: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }

    /**
     * Mark order as ready (move from IN_KITCHEN to SERVED)
     * Convenience endpoint for common chef action
     */
    @PutMapping("/orders/{orderId}/ready")
    public ResponseEntity<Map<String, Object>> markOrderReady(@PathVariable String orderId) {
        try {
            log.info("Chef marking order as ready: {}", orderId);
            
            Order updatedOrder = orderService.updateOrderStatus(orderId, OrderStatus.SERVED);
            
            log.info("Order marked as ready by chef: {}", orderId);
            return ResponseEntity.ok(Map.of(
                    "message", "Order marked as ready and served",
                    "orderId", updatedOrder.getOrderId(),
                    "status", updatedOrder.getOrderStatus(),
                    "tableNumber", updatedOrder.getTableNumber()
            ));
            
        } catch (Exception e) {
            log.error("Error marking order as ready: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Start preparing order (move from PLACED to IN_KITCHEN)
     * Convenience endpoint for common chef action
     */
    @PutMapping("/orders/{orderId}/start")
    public ResponseEntity<Map<String, Object>> startPreparingOrder(@PathVariable String orderId) {
        try {
            log.info("Chef starting to prepare order: {}", orderId);
            
            Order updatedOrder = orderService.updateOrderStatus(orderId, OrderStatus.IN_KITCHEN);
            
            log.info("Order preparation started by chef: {}", orderId);
            return ResponseEntity.ok(Map.of(
                    "message", "Order preparation started",
                    "orderId", updatedOrder.getOrderId(),
                    "status", updatedOrder.getOrderStatus(),
                    "tableNumber", updatedOrder.getTableNumber()
            ));
            
        } catch (Exception e) {
            log.error("Error starting order preparation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get order details by ID
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        try {
            log.info("Chef fetching order details: {}", orderId);
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            log.error("Order not found: {}", orderId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error fetching order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
}