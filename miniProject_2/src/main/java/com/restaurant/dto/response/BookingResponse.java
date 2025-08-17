package org.restaurant.dto.response;

import org.restaurant.enums.BookingStatus;

import java.time.LocalDateTime;

public class BookingResponse {

    private Long id;
    private String customerName;
    private LocalDateTime bookingTime;
    private Integer tableNumber;
    private Integer numberOfGuests;
    private BookingStatus status;
    private LocalDateTime createdAt;

    // Constructors
    public BookingResponse() {}

    public BookingResponse(Long id, String customerName, LocalDateTime bookingTime,
                           Integer tableNumber, Integer numberOfGuests, BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.bookingTime = bookingTime;
        this.tableNumber = tableNumber;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}