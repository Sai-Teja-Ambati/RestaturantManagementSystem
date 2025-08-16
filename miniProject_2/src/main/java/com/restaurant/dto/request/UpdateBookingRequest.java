package org.restaurant.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class UpdateBookingRequest {

    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;

    @Future(message = "Booking time must be in the future")
    private LocalDateTime bookingTime;

    private Integer tableNumber;

    @Min(value = 1, message = "Number of guests must be at least 1")
    private Integer numberOfGuests;

    // Constructors
    public UpdateBookingRequest() {}

    public UpdateBookingRequest(String customerName, LocalDateTime bookingTime,
                                Integer tableNumber, Integer numberOfGuests) {
        this.customerName = customerName;
        this.bookingTime = bookingTime;
        this.tableNumber = tableNumber;
        this.numberOfGuests = numberOfGuests;
    }

    // Getters and Setters
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
}