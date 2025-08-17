package org.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restaurant.dto.request.CreateOrderRequest;
import org.restaurant.dto.request.UpdateOrderStatusRequest;
import org.restaurant.dto.response.OrderResponse;
import org.restaurant.dto.response.SuccessResponse;
import org.restaurant.enums.OrderStatus;
import org.restaurant.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER", "WAITER", "CHEF"})
    void getAllOrders_ShouldReturnList() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Collections.singletonList(orderResponse));

        mockMvc.perform(get("/orders").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER", "WAITER", "CHEF"})
    void getOrderById_ShouldReturnOrder() throws Exception {
        when(orderService.getOrderById(anyLong())).thenReturn(orderResponse);

        mockMvc.perform(get("/orders/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER", "WAITER", "CHEF"})
    void updateOrderStatus_ShouldReturnUpdatedOrder() throws Exception {
        UpdateOrderStatusRequest updateRequest = new UpdateOrderStatusRequest();
        updateRequest.setStatus(OrderStatus.SERVED);

        when(orderService.updateOrderStatus(anyLong(), any(UpdateOrderStatusRequest.class))).thenReturn(orderResponse);

        mockMvc.perform(put("/orders/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER", "WAITER", "CHEF"})
    void getOrdersByTableNumber_ShouldReturnOrders() throws Exception {
        when(orderService.getOrdersByTableNumber(anyInt())).thenReturn(Collections.singletonList(orderResponse));

        mockMvc.perform(get("/orders/table/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER", "WAITER", "CHEF"})
    void getOrdersByStatus_ShouldReturnOrders() throws Exception {
        when(orderService.getOrdersByStatus(any(OrderStatus.class))).thenReturn(Collections.singletonList(orderResponse));

        mockMvc.perform(get("/orders/status/SERVED").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER", "WAITER"})
    void cancelOrder_ShouldReturnSuccess() throws Exception {
        doNothing().when(orderService).cancelOrder(anyLong());

        mockMvc.perform(delete("/orders/1").with(csrf()))
                .andExpect(status().isOk());
    }
}
