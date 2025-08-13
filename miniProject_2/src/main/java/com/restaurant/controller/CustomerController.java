package com.restaurant.controller;

import com.restaurant.dto.OrderRequest;
import com.restaurant.dto.ReservationRequest;
import com.restaurant.entity.Order;
import com.restaurant.entity.RestaurantTable;
import com.restaurant.entity.TableReservation;
import com.restaurant.entity.User;
import com.restaurant.service.MenuService;
import com.restaurant.service.OrderService;
import com.restaurant.service.TableReservationService;
import com.restaurant.service.TableService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TableService tableService;

    @Autowired
    private TableReservationService reservationService;

    @GetMapping("/menu")
    public ResponseEntity<Map<String, Object>> getMenu() {
        try {
            Map<String, Object> menu = menuService.getFullMenu();
            return ResponseEntity.ok(menu);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/tables/available")
    public ResponseEntity<List<RestaurantTable>> getAvailableTables(@RequestParam(required = false) Integer capacity) {
        try {
            List<RestaurantTable> tables;
            if (capacity != null) {
                tables = tableService.getAvailableTablesByCapacity(capacity);
            } else {
                tables = tableService.getAvailableTables();
            }
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/reservations")
    public ResponseEntity<Map<String, Object>> bookTable(@Valid @RequestBody ReservationRequest request, 
                                                        Authentication authentication) {
        try {
            User customer = (User) authentication.getPrincipal();
            TableReservation reservation = reservationService.createReservation(
                    customer, request.getTableNumber(), request.getStartTime(), request.getEndTime(), request.getNotes());
            return ResponseEntity.ok(Map.of(
                    "message", "Table booked successfully",
                    "reservationId", reservation.getId(),
                    "tableNumber", reservation.getTableNumber()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<TableReservation>> getMyReservations(Authentication authentication) {
        try {
            User customer = (User) authentication.getPrincipal();
            List<TableReservation> reservations = reservationService.getCustomerReservations(customer.getId());
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> placeOrder(@Valid @RequestBody OrderRequest request, 
                                                         Authentication authentication) {
        try {
            User customer = (User) authentication.getPrincipal();
            Order order = orderService.createOrder(customer, request.getItems(), request.getTableNumber(), 
                                                  request.getSpecialInstructions());
            return ResponseEntity.ok(Map.of(
                    "message", "Order placed successfully",
                    "orderId", order.getOrderId(),
                    "total", order.getBillTotal()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getMyOrders(Authentication authentication) {
        try {
            User customer = (User) authentication.getPrincipal();
            List<Order> orders = orderService.getCustomerOrders(customer.getId());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable String orderId, Authentication authentication) {
        try {
            User customer = (User) authentication.getPrincipal();
            Order order = orderService.getOrderByIdAndCustomer(orderId, customer.getId());
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}