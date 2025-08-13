package com.restaurant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Order is required")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    @NotNull(message = "Table is required")
    private RestaurantTable table;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public OrderTable(Order order, RestaurantTable table) {
        this.order = order;
        this.table = table;
        this.createdAt = LocalDateTime.now();
    }
}