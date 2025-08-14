package com.restaurant.service.impl;

import com.restaurant.entity.Order;
import com.restaurant.entity.User;
import com.restaurant.enums.OrderStatus;
import com.restaurant.repository.OrderRepository;
import com.restaurant.service.InventoryService;
import com.restaurant.service.OrderService;
import com.restaurant.util.RecipeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryService inventoryService;

    @Override
    public Order createOrder(User customer, List<Map<String, Object>> items, Integer tableNumber, String specialInstructions) {
        // Validate items and check inventory
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (Map<String, Object> item : items) {
            String itemName = (String) item.get("itemName");
            Integer quantity = (Integer) item.get("quantity");
            Double price = (Double) item.get("price");
            
            if (itemName == null || quantity == null || price == null) {
                throw new RuntimeException("Invalid item data");
            }
            
            // Check if we have enough ingredients
            Map<String, Integer> recipe = RecipeUtil.getRecipeIngredients(itemName);
            if (recipe != null) {
                for (Map.Entry<String, Integer> ingredient : recipe.entrySet()) {
                    int requiredQuantity = ingredient.getValue() * quantity;
                    if (!inventoryService.hasEnoughIngredient(ingredient.getKey(), requiredQuantity)) {
                        throw new RuntimeException("Insufficient inventory for " + ingredient.getKey());
                    }
                }
            }
            
            subtotal = subtotal.add(BigDecimal.valueOf(price * quantity));
        }
        
        // Generate unique order ID
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Create order
        Order order = new Order(orderId, customer, items, subtotal);
        order.setTableNumber(tableNumber);
        order.setSpecialInstructions(specialInstructions);
        order.calculateTotals();
        
        // Deduct ingredients from inventory
        for (Map<String, Object> item : items) {
            String itemName = (String) item.get("itemName");
            Integer quantity = (Integer) item.get("quantity");
            
            Map<String, Integer> recipe = RecipeUtil.getRecipeIngredients(itemName);
            if (recipe != null) {
                for (Map.Entry<String, Integer> ingredient : recipe.entrySet()) {
                    int requiredQuantity = ingredient.getValue() * quantity;
                    inventoryService.reduceIngredientQuantity(ingredient.getKey(), requiredQuantity);
                }
            }
        }
        
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerIdOrderByTimestampDesc(customerId);
    }

    @Override
    public Order getOrderByIdAndCustomer(String orderId, Long customerId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Order does not belong to customer");
        }
        
        return order;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getActiveOrders() {
        return orderRepository.findActiveOrders();
    }

    @Override
    public List<Order> getReadyOrders() {
        return orderRepository.findReadyOrders();
    }

    @Override
    public List<Order> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrdersBetweenDates(startDate, endDate);
    }

    @Override
    public long countOrdersSince(LocalDateTime since) {
        return orderRepository.countOrdersSince(since);
    }

    @Override
    public Double getTotalRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        Double revenue = orderRepository.getTotalRevenueBetweenDates(startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }
}