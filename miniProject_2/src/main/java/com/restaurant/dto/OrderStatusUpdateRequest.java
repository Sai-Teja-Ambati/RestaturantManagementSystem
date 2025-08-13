package com.restaurant.dto;

import com.restaurant.entity.Order;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {
    
    @NotNull(message = "Order status is required")
    private Order.OrderStatus status;
}