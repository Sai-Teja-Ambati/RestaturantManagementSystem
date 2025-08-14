package com.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.enums.ReservationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "table_reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"reservations", "orderTables"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_id", nullable = false)
    @NotNull(message = "Table is required")
    private RestaurantTable table;

    @JsonIgnoreProperties({"orders", "reservations", "password"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer is required")
    private User customer;

    @Builder.Default
    @Column(name = "reservation_time")
    private LocalDateTime reservationTime = LocalDateTime.now();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "table_number")
    private Integer tableNumber;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    // Custom constructor for creating reservations with automatic field population
    public TableReservation(RestaurantTable table, User customer, LocalDateTime startTime, LocalDateTime endTime) {
        this.table = table;
        this.customer = customer;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationTime = LocalDateTime.now();
        this.status = ReservationStatus.ACTIVE;
        this.tableNumber = table != null ? table.getTableNumber() : null;
    }

    // Custom setter to maintain tableNumber consistency
    public void setTable(RestaurantTable table) {
        this.table = table;
        this.tableNumber = table != null ? table.getTableNumber() : null;
    }
}