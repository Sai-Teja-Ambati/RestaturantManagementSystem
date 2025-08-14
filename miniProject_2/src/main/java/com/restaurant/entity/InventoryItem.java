package com.restaurant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Initial quantity is required")
    @Min(value = 0, message = "Initial quantity cannot be negative")
    @Column(name = "initial_quantity", nullable = false)
    private Integer initialQuantity;

    @Builder.Default
    @Min(value = 0, message = "Minimum threshold cannot be negative")
    @Column(name = "min_threshold")
    private Integer minThreshold = 10;

    @Builder.Default
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Custom constructors for convenience
    public InventoryItem(String name, Integer quantity, Integer initialQuantity) {
        this.name = name;
        this.quantity = quantity;
        this.initialQuantity = initialQuantity;
        this.minThreshold = 10;
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public InventoryItem(String name, Integer quantity, Integer initialQuantity, Integer minThreshold) {
        this.name = name;
        this.quantity = quantity;
        this.initialQuantity = initialQuantity;
        this.minThreshold = minThreshold;
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isLowStock() {
        return quantity <= minThreshold;
    }

    public void updateQuantity(Integer newQuantity) {
        this.quantity = newQuantity;
        this.lastUpdated = LocalDateTime.now();
    }

    public void reduceQuantity(Integer amount) {
        if (amount > 0 && this.quantity >= amount) {
            this.quantity -= amount;
            this.lastUpdated = LocalDateTime.now();
        }
    }

    public void addQuantity(Integer amount) {
        if (amount > 0) {
            this.quantity += amount;
            this.lastUpdated = LocalDateTime.now();
        }
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.lastUpdated = LocalDateTime.now();
    }
}