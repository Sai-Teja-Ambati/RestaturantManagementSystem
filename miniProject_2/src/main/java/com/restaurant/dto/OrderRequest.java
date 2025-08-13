package com.restaurant.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    
    @NotEmpty(message = "Order items cannot be empty")
    private List<Map<String, Object>> items;
    
    private Integer tableNumber;
    
    private String specialInstructions;
}