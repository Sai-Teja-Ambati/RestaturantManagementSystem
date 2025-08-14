package com.restaurant.service;

import com.restaurant.entity.Order;
import com.restaurant.entity.User;
import com.restaurant.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {
    
    Order createOrder(User customer, List<Map<String, Object>> items, Integer tableNumber, String specialInstructions);
    
    Order updateOrderStatus(String orderId, OrderStatus status);
    
    List<Order> getCustomerOrders(Long customerId);
    
    Order getOrderByIdAndCustomer(String orderId, Long customerId);
    
    List<Order> getAllOrders();
    
    List<Order> getActiveOrders();
    
    List<Order> getReadyOrders();
    
    List<Order> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    long countOrdersSince(LocalDateTime since);
    
    Double getTotalRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    Order getOrderById(String orderId);
    
    List<Order> getOrdersByStatus(OrderStatus status);
}