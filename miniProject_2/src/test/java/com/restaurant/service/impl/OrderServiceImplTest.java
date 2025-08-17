package org.restaurant.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.restaurant.dto.request.CreateOrderRequest;
import org.restaurant.dto.request.UpdateOrderStatusRequest;
import org.restaurant.entities.*;
import org.restaurant.enums.OrderStatus;
import org.restaurant.enums.TableStatus;
import org.restaurant.exceptions.BusinessLogicException;
import org.restaurant.exceptions.ResourceNotFoundException;
import org.restaurant.repository.MenuItemRepository;
import org.restaurant.repository.OrderRepository;
import org.restaurant.repository.RestaurantTableRepository;
import org.restaurant.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock private OrderRepository orderRepository;
    @Mock private MenuItemRepository menuItemRepository;
    @Mock private RestaurantTableRepository tableRepository;
    @Mock private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("waiter1");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void createOrder_TableNotFound_Throws() {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setTableNumber(5);
        when(tableRepository.findByTableNumber(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(req));
    }

    @Test
    void getOrderById_NotFound_Throws() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void getOrdersByTableNumber_ReturnsList() {
        Order order = new Order(); order.setOrderId(1L);
        when(orderRepository.findByTableNumber(7)).thenReturn(List.of(order));
        assertEquals(1, orderService.getOrdersByTableNumber(7).size());
    }

    @Test
    void getOrdersByStatus_ReturnsList() {
        Order order = new Order(); order.setOrderId(2L);
        when(orderRepository.findByStatus(OrderStatus.PLACED)).thenReturn(List.of(order));
        assertEquals(1, orderService.getOrdersByStatus(OrderStatus.PLACED).size());
    }

    @Test
    void moveToKitchen_WrongStatus_Throws() {
        Order order = new Order(); order.setStatus(OrderStatus.SERVED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(BusinessLogicException.class, () -> orderService.moveToKitchen(1L));
    }

    @Test
    void cancelOrder_ServedOrder_Throws() {
        Order order = new Order(); order.setStatus(OrderStatus.SERVED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(BusinessLogicException.class, () -> orderService.cancelOrder(1L));
    }
}
