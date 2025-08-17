package org.restaurant.service.impl;

import org.restaurant.dto.request.CreateOrderRequest;
import org.restaurant.dto.request.UpdateOrderStatusRequest;
import org.restaurant.dto.response.OrderResponse;
import org.restaurant.entities.*;
import org.restaurant.enums.OrderStatus;
import org.restaurant.enums.TableStatus;
import org.restaurant.exceptions.ResourceNotFoundException;
import org.restaurant.exceptions.BusinessLogicException;
import org.restaurant.repository.OrderRepository;
import org.restaurant.repository.MenuItemRepository;
import org.restaurant.repository.RestaurantTableRepository;
import org.restaurant.service.OrderService;
import org.restaurant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantTableRepository tableRepository;

    @Autowired
    private UserService userService;

    /**
     * Create a new order
     */
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Validate table number exists and is available
        RestaurantTable table = tableRepository.findByTableNumber(Long.valueOf(request.getTableNumber()))
                .orElseThrow(() -> new ResourceNotFoundException("Table not found: " + request.getTableNumber()));

        // Get current user (waiter)
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User waiter = userService.getUserEntityById(userService.getUserByUsername(currentUsername).getId());

        // Create new order
        Order order = new Order();
        order.setTableNumber(request.getTableNumber());
        order.setRestaurantTable(table);
        order.setWaiter(waiter);
        order.setStatus(OrderStatus.PLACED);

        // Save order first to get ID
        Order savedOrder = orderRepository.save(order);

        // Add order items
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + itemRequest.getMenuItemId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());

            savedOrder.addOrderItem(orderItem);
        }

        // Update table status to occupied
        if (table.getStatus() != TableStatus.OCCUPIED) {
            table.setStatus(TableStatus.OCCUPIED);
            tableRepository.save(table);
        }

        // Save order with items
        Order finalOrder = orderRepository.save(savedOrder);

        return convertToOrderResponse(finalOrder);
    }

    /**
     * Get all orders
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return convertToOrderResponse(order);
    }

    /**
     * Update order status
     */
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Validate status transition
        validateStatusTransition(order.getStatus(), request.getStatus());

        order.setStatus(request.getStatus());

        // If order is served, free up the table
        if (request.getStatus() == OrderStatus.SERVED) {
            if (order.getRestaurantTable() != null) {
                order.getRestaurantTable().setStatus(TableStatus.AVAILABLE);
                tableRepository.save(order.getRestaurantTable());
            }
        }

        Order updatedOrder = orderRepository.save(order);
        return convertToOrderResponse(updatedOrder);
    }

    /**
     * Get orders by table number
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByTableNumber(Integer tableNumber) {
        List<Order> orders = orderRepository.findByTableNumber(tableNumber);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by waiter ID
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByWaiterId(Long waiterId) {
        List<Order> orders = orderRepository.findByWaiter_UserId(waiterId);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by date
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByDate(LocalDateTime date) {
        List<Order> orders = orderRepository.findByOrderDate(date);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get kitchen orders (IN_KITCHEN status)
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getKitchenOrders() {
        List<OrderStatus> kitchenStatuses = List.of(OrderStatus.PLACED, OrderStatus.IN_KITCHEN);
        List<Order> orders = orderRepository.findByStatusInOrderByCreatedAtAsc(kitchenStatuses);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mark order as in kitchen
     */
    public OrderResponse moveToKitchen(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new BusinessLogicException("Only placed orders can be moved to kitchen");
        }

        order.setStatus(OrderStatus.IN_KITCHEN);
        Order updatedOrder = orderRepository.save(order);
        return convertToOrderResponse(updatedOrder);
    }

    /**
     * Mark order as served
     */
    public OrderResponse markAsServed(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.IN_KITCHEN) {
            throw new BusinessLogicException("Only kitchen orders can be marked as served");
        }

        order.setStatus(OrderStatus.SERVED);

        // Free up the table
        if (order.getRestaurantTable() != null) {
            order.getRestaurantTable().setStatus(TableStatus.AVAILABLE);
            tableRepository.save(order.getRestaurantTable());
        }

        Order updatedOrder = orderRepository.save(order);
        return convertToOrderResponse(updatedOrder);
    }

    /**
     * Cancel order
     */
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() == OrderStatus.SERVED) {
            throw new BusinessLogicException("Cannot cancel served orders");
        }

        // Free up the table if no other active orders
        if (order.getRestaurantTable() != null) {
            List<Order> activeOrders = orderRepository.findByRestaurantTable_TableId(order.getRestaurantTable().getTableId())
                    .stream()
                    .filter(o -> o.getStatus() != OrderStatus.SERVED && !o.getOrderId().equals(orderId))
                    .collect(Collectors.toList());

            if (activeOrders.isEmpty()) {
                order.getRestaurantTable().setStatus(TableStatus.AVAILABLE);
                tableRepository.save(order.getRestaurantTable());
            }
        }

        orderRepository.deleteById(orderId);
    }

    /**
     * Get current user's orders (for waiters)
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getCurrentUserOrders() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getUserEntityById(userService.getUserByUsername(currentUsername).getId());
        return getOrdersByWaiterId(currentUser.getUserId());
    }

    // Helper methods

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        switch (currentStatus) {
            case PLACED:
                if (newStatus != OrderStatus.IN_KITCHEN && newStatus != OrderStatus.PLACED) {
                    throw new BusinessLogicException("Placed orders can only be moved to kitchen");
                }
                break;
            case IN_KITCHEN:
                if (newStatus != OrderStatus.SERVED && newStatus != OrderStatus.IN_KITCHEN) {
                    throw new BusinessLogicException("Kitchen orders can only be marked as served");
                }
                break;
            case SERVED:
                throw new BusinessLogicException("Served orders cannot be modified");
        }
    }

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getOrderId());
        response.setTableNumber(order.getTableNumber());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(order.getItems());

        if (order.getWaiter() != null) {
            response.setWaiterName(order.getWaiter().getFullName());
        }

        if (order.getRestaurantTable() != null) {
            response.setTableId(order.getRestaurantTable().getTableId());
        }

        return response;
    }
}