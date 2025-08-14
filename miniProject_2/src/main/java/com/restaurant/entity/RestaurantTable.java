package com.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"reservations", "orderTables"})
@ToString(exclude = {"reservations", "orderTables"})
public class RestaurantTable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Table number is required")
    @Column(name = "table_number", unique = true, nullable = false)
    private Integer tableNumber;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Builder.Default
    @Column(nullable = false)
    private Integer capacity = 4;

    @Builder.Default
    @Column(name = "is_occupied")
    private Boolean isOccupied = false;

    @Builder.Default
    @Column(name = "is_served")
    private Boolean isServed = false;

    @Column(name = "booking_start_time")
    private LocalDateTime bookingStartTime;

    @Column(name = "booking_end_time")
    private LocalDateTime bookingEndTime;

    @JsonIgnore
    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TableReservation> reservations;

    @JsonIgnore
    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderTable> orderTables;

    // Custom constructor for convenience - @Builder.Default handles default values
    public RestaurantTable(Integer tableNumber, Integer capacity) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.isOccupied = false;
        this.isServed = false;
    }
}