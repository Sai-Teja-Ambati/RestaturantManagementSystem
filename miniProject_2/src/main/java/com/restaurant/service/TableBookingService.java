package com.restaurant.service;

import com.restaurant.entity.TableBooking;
import com.restaurant.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TableBookingService {
    
    TableBooking createBooking(String customerName, LocalDateTime bookingTime, Integer tableNumber, Integer numberOfGuests);
    
    TableBooking createBooking(String customerName, LocalDateTime bookingTime, Integer tableNumber, Integer numberOfGuests, String notes);
    
    List<TableBooking> getAllBookings();
    
    Optional<TableBooking> getBookingById(Long id);
    
    TableBooking updateBooking(Long id, String customerName, LocalDateTime bookingTime, Integer tableNumber, Integer numberOfGuests, String notes);
    
    void cancelBooking(Long id);
    
    void deleteBooking(Long id);
    
    List<TableBooking> getBookingsByStatus(ReservationStatus status);
    
    List<TableBooking> getBookingsByTableNumber(Integer tableNumber);
    
    List<TableBooking> getBookingsByCustomerName(String customerName);
    
    List<TableBooking> getBookingsByDateRange(LocalDateTime startTime, LocalDateTime endTime);
    
    boolean isTableAvailable(Integer tableNumber, LocalDateTime bookingTime);
    
    List<TableBooking> getActiveBookings();
    
    long getBookingCount();
    
    long getActiveBookingCount();
}