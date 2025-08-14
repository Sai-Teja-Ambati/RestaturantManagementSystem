package com.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotNull(message = "Booking time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingTime;
    
    @NotNull(message = "Table number is required")
    @Positive(message = "Table number must be positive")
    private Integer tableNumber;
    
    @NotNull(message = "Number of guests is required")
    @Positive(message = "Number of guests must be positive")
    private Integer numberOfGuests;
    
    private String notes;
}