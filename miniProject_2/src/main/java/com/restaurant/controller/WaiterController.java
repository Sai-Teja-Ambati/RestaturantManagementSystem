package com.restaurant.controller;

import com.restaurant.dto.OrderStatusUpdateRequest;
import com.restaurant.entity.Order;
import com.restaurant.entity.RestaurantTable;
import com.restaurant.entity.TableReservation;
import com.restaurant.service.OrderService;
import com.restaurant.service.TableReservationService;
import com.restaurant.service.TableService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/waiter")
@CrossOrigin(origins = "*")
public class WaiterController {

    @Autowired
    private TableService tableService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TableReservationService reservationService;

    @GetMapping("/tables")
    public ResponseEntity<List<RestaurantTable>> getAllTables() {
        try {
            List<RestaurantTable> tables = tableService.getAllTables();
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/tables/vacant")
    public ResponseEntity<List<RestaurantTable>> getVacantTables() {
        try {
            List<RestaurantTable> tables = tableService.getAvailableTables();
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/tables/occupied")
    public ResponseEntity<List<RestaurantTable>> getOccupiedTables() {
        try {
            List<RestaurantTable> tables = tableService.getOccupiedTables();
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/tables/{tableNumber}/serve")
    public ResponseEntity<Map<String, Object>> serveTable(@PathVariable Integer tableNumber) {
        try {
            RestaurantTable table = tableService.serveTable(tableNumber);
            return ResponseEntity.ok(Map.of(
                    "message", "Table " + tableNumber + " marked as served",
                    "tableNumber", table.getTableNumber(),
                    "isServed", table.getIsServed()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/tables/{tableNumber}/occupy")
    public ResponseEntity<Map<String, Object>> occupyTable(@PathVariable Integer tableNumber) {
        try {
            RestaurantTable table = tableService.occupyTable(tableNumber);
            return ResponseEntity.ok(Map.of(
                    "message", "Table " + tableNumber + " marked as occupied",
                    "tableNumber", table.getTableNumber(),
                    "isOccupied", table.getIsOccupied()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/tables/{tableNumber}/free")
    public ResponseEntity<Map<String, Object>> freeTable(@PathVariable Integer tableNumber) {
        try {
            RestaurantTable table = tableService.freeTable(tableNumber);
            return ResponseEntity.ok(Map.of(
                    "message", "Table " + tableNumber + " is now free",
                    "tableNumber", table.getTableNumber(),
                    "isOccupied", table.getIsOccupied(),
                    "isServed", table.getIsServed()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/orders/active")
    public ResponseEntity<List<Order>> getActiveOrders() {
        try {
            List<Order> orders = orderService.getActiveOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/orders/ready")
    public ResponseEntity<List<Order>> getReadyOrders() {
        try {
            List<Order> orders = orderService.getReadyOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@PathVariable String orderId, 
                                                               @Valid @RequestBody OrderStatusUpdateRequest request) {
        try {
            Order order = orderService.updateOrderStatus(orderId, request.getStatus());
            return ResponseEntity.ok(Map.of(
                    "message", "Order status updated successfully",
                    "orderId", order.getOrderId(),
                    "status", order.getOrderStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<TableReservation>> getAllReservations() {
        try {
            List<TableReservation> reservations = reservationService.getAllReservations();
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/reservations/active")
    public ResponseEntity<List<TableReservation>> getActiveReservations() {
        try {
            List<TableReservation> reservations = reservationService.getActiveReservations();
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/reservations/{reservationId}/complete")
    public ResponseEntity<Map<String, Object>> completeReservation(@PathVariable Long reservationId) {
        try {
            TableReservation reservation = reservationService.completeReservation(reservationId);
            return ResponseEntity.ok(Map.of(
                    "message", "Reservation completed successfully",
                    "reservationId", reservation.getId(),
                    "status", reservation.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}