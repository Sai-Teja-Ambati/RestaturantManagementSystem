package com.restaurant.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    @NotNull(message = "Table is required")
    private RestaurantTable table;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public TableReservation(RestaurantTable table, User customer, LocalDateTime startTime, LocalDateTime endTime) {
        this.table = table;
        this.customer = customer;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationTime = LocalDateTime.now();
        this.status = ReservationStatus.ACTIVE;
        this.tableNumber = table.getTableNumber();
    }

    public void setTable(RestaurantTable table) {
        this.table = table;
        if (table != null) {
            this.tableNumber = table.getTableNumber();
        }
    }

    public enum ReservationStatus {
        ACTIVE, COMPLETED, CANCELLED
    }
}