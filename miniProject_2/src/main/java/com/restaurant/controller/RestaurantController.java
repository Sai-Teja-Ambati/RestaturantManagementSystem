package com.restaurant.controller;

import com.restaurant.dto.BookingRequest;
import com.restaurant.dto.OrderRequest;
import com.restaurant.dto.RegisterRequest;
import com.restaurant.entity.Order;
import com.restaurant.entity.TableBooking;
import com.restaurant.entity.User;
import com.restaurant.enums.OrderStatus;
import com.restaurant.service.OrderService;
import com.restaurant.service.TableBookingService;
import com.restaurant.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Slf4j
public class RestaurantController {

    @Autowired
    private UserService userService;

    @Autowired
    private TableBookingService bookingService;

    @Autowired
    private OrderService orderService;

    // ==================== USER MANAGEMENT ====================

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("Creating user with username: {}", request.getUsername());
            User user = userService.createUser(request.getUsername(), request.getName(), 
                                             request.getEmail(), request.getPassword(), request.getRole());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "User created successfully",
                    "userId", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole()
            ));
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            log.info("Fetching all users");
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ==================== TABLE BOOKING MANAGEMENT ====================

    @PostMapping("/bookings")
    public ResponseEntity<Map<String, Object>> createBooking(@Valid @RequestBody BookingRequest request) {
        try {
            log.info("Creating booking for customer: {} at table: {}", request.getCustomerName(), request.getTableNumber());
            TableBooking booking = bookingService.createBooking(
                    request.getCustomerName(),
                    request.getBookingTime(),
                    request.getTableNumber(),
                    request.getNumberOfGuests(),
                    request.getNotes()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Booking created successfully",
                    "bookingId", booking.getId(),
                    "customerName", booking.getCustomerName(),
                    "tableNumber", booking.getTableNumber(),
                    "bookingTime", booking.getBookingTime()
            ));
        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<TableBooking>> getAllBookings() {
        try {
            log.info("Fetching all bookings");
            List<TableBooking> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error fetching bookings: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<TableBooking> getBookingById(@PathVariable Long id) {
        try {
            log.info("Fetching booking with ID: {}", id);
            Optional<TableBooking> booking = bookingService.getBookingById(id);
            return booking.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching booking: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/bookings/{id}")
    public ResponseEntity<Map<String, Object>> updateBooking(@PathVariable Long id, 
                                                           @Valid @RequestBody BookingRequest request) {
        try {
            log.info("Updating booking with ID: {}", id);
            TableBooking booking = bookingService.updateBooking(
                    id,
                    request.getCustomerName(),
                    request.getBookingTime(),
                    request.getTableNumber(),
                    request.getNumberOfGuests(),
                    request.getNotes()
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Booking updated successfully",
                    "bookingId", booking.getId(),
                    "customerName", booking.getCustomerName(),
                    "tableNumber", booking.getTableNumber()
            ));
        } catch (Exception e) {
            log.error("Error updating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Map<String, Object>> deleteBooking(@PathVariable Long id) {
        try {
            log.info("Deleting booking with ID: {}", id);
            bookingService.deleteBooking(id);
            return ResponseEntity.ok(Map.of("message", "Booking deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting booking: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ORDER MANAGEMENT ====================

    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody OrderRequest request) {
        try {
            log.info("Creating order for table: {}", request.getTableNumber());
            // For now, create order without customer authentication
            Order order = orderService.createOrder(null, request.getItems(), 
                                                  request.getTableNumber(), request.getSpecialInstructions());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Order placed successfully",
                    "orderId", order.getOrderId(),
                    "tableNumber", order.getTableNumber(),
                    "total", order.getBillTotal(),
                    "status", order.getOrderStatus()
            ));
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            log.info("Fetching all orders");
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching orders: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@PathVariable String id, 
                                                               @RequestBody Map<String, String> request) {
        try {
            log.info("Updating order status for order ID: {}", id);
            String statusStr = request.get("status");
            if (statusStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }
            
            OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());
            Order order = orderService.updateOrderStatus(id, status);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Order status updated successfully",
                    "orderId", order.getOrderId(),
                    "status", order.getOrderStatus()
            ));
        } catch (IllegalArgumentException e) {
            log.error("Invalid order status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid order status"));
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}