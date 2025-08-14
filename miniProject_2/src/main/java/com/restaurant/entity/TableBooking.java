package com.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.enums.ReservationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "table_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableBooking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name is required")
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @NotNull(message = "Booking time is required")
    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    @NotNull(message = "Table number is required")
    @Positive(message = "Table number must be positive")
    @Column(name = "table_number", nullable = false)
    private Integer tableNumber;

    @NotNull(message = "Number of guests is required")
    @Positive(message = "Number of guests must be positive")
    @Column(name = "number_of_guests", nullable = false)
    private Integer numberOfGuests;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Optional: Link to User if you want to maintain user relationships
    @JsonIgnoreProperties({"orders", "reservations", "password"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private User customer;

    // Optional: Link to RestaurantTable if you want to maintain table relationships
    @JsonIgnoreProperties({"reservations", "orderTables"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    // Custom constructor for creating bookings with required fields
    public TableBooking(String customerName, LocalDateTime bookingTime, Integer tableNumber, Integer numberOfGuests) {
        this.customerName = customerName;
        this.bookingTime = bookingTime;
        this.tableNumber = tableNumber;
        this.numberOfGuests = numberOfGuests;
        this.createdAt = LocalDateTime.now();
        this.status = ReservationStatus.ACTIVE;
    }

    // Custom setter to maintain tableNumber consistency
    public void setTable(RestaurantTable table) {
        this.table = table;
        this.tableNumber = table != null ? table.getTableNumber() : null;
    }
}