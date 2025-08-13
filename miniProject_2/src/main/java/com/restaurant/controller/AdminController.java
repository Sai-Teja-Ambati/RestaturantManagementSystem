package com.restaurant.controller;

import com.restaurant.entity.InventoryItem;
import com.restaurant.entity.Order;
import com.restaurant.entity.RestaurantTable;
import com.restaurant.entity.User;
import com.restaurant.service.InventoryService;
import com.restaurant.service.OrderService;
import com.restaurant.service.TableService;
import com.restaurant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TableService tableService;

    // Inventory Management
    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryItem>> getAllInventory() {
        try {
            List<InventoryItem> inventory = inventoryService.getAllInventoryItems();
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/inventory/low-stock")
    public ResponseEntity<List<InventoryItem>> getLowStockItems() {
        try {
            List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();
            return ResponseEntity.ok(lowStockItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/inventory/restore")
    public ResponseEntity<Map<String, Object>> restoreInventory() {
        try {
            inventoryService.restoreInventoryFromFile();
            return ResponseEntity.ok(Map.of(
                    "message", "Inventory restored successfully from initial inventory file"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/inventory/reload")
    public ResponseEntity<Map<String, Object>> reloadCurrentInventory() {
        try {
            inventoryService.loadCurrentInventoryFromFile();
            return ResponseEntity.ok(Map.of(
                    "message", "Current inventory reloaded successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/inventory/{itemName}/quantity")
    public ResponseEntity<Map<String, Object>> updateInventoryQuantity(@PathVariable String itemName, 
                                                                      @RequestParam Integer quantity) {
        try {
            InventoryItem item = inventoryService.updateItemQuantity(itemName, quantity);
            return ResponseEntity.ok(Map.of(
                    "message", "Inventory updated successfully",
                    "itemName", item.getName(),
                    "newQuantity", item.getQuantity()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/users/waiters")
    public ResponseEntity<List<User>> getAllWaiters() {
        try {
            List<User> waiters = userService.findByRole(User.Role.WAITER);
            return ResponseEntity.ok(waiters);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/users/customers")
    public ResponseEntity<List<User>> getAllCustomers() {
        try {
            List<User> customers = userService.findByRole(User.Role.CUSTOMER);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Order Management
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/orders/today")
    public ResponseEntity<List<Order>> getTodayOrders() {
        try {
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            List<Order> orders = orderService.getOrdersBetweenDates(startOfDay, endOfDay);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Table Management
    @GetMapping("/tables")
    public ResponseEntity<List<RestaurantTable>> getAllTables() {
        try {
            List<RestaurantTable> tables = tableService.getAllTables();
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/tables")
    public ResponseEntity<Map<String, Object>> addTable(@RequestParam Integer tableNumber, 
                                                       @RequestParam Integer capacity) {
        try {
            RestaurantTable table = tableService.addTable(tableNumber, capacity);
            return ResponseEntity.ok(Map.of(
                    "message", "Table added successfully",
                    "tableNumber", table.getTableNumber(),
                    "capacity", table.getCapacity()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Dashboard Statistics
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            
            Map<String, Object> stats = Map.of(
                    "totalUsers", userService.getAllUsers().size(),
                    "totalCustomers", userService.countByRole(User.Role.CUSTOMER),
                    "totalWaiters", userService.countByRole(User.Role.WAITER),
                    "totalTables", tableService.getAllTables().size(),
                    "occupiedTables", tableService.getOccupiedTables().size(),
                    "availableTables", tableService.getAvailableTables().size(),
                    "todayOrders", orderService.countOrdersSince(today),
                    "activeOrders", orderService.getActiveOrders().size(),
                    "lowStockItems", inventoryService.getLowStockItems().size(),
                    "outOfStockItems", inventoryService.getOutOfStockItems().size()
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}