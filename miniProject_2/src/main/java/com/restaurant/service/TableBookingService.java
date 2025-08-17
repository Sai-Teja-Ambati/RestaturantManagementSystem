package org.restaurant.service;

import org.restaurant.dto.request.CreateBookingRequest;
import org.restaurant.dto.request.UpdateBookingRequest;
import org.restaurant.dto.response.BookingResponse;
import org.restaurant.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for table booking operations
 */
public interface TableBookingService {

    /**
     * Create a new booking
     * @param request Booking creation request
     * @return Booking response
     */
    BookingResponse createBooking(CreateBookingRequest request);

    /**
     * Get all bookings
     * @return List of booking responses
     */
    List<BookingResponse> getAllBookings();

    /**
     * Get booking by ID
     * @param bookingId Booking ID
     * @return Booking response
     */
    BookingResponse getBookingById(Long bookingId);

    /**
     * Update booking
     * @param bookingId Booking ID
     * @param request Booking update request
     * @return Updated booking response
     */
    BookingResponse updateBooking(Long bookingId, UpdateBookingRequest request);

    /**
     * Cancel booking (soft delete)
     * @param bookingId Booking ID
     */
    void cancelBooking(Long bookingId);

    /**
     * Delete booking permanently
     * @param bookingId Booking ID
     */
    void deleteBooking(Long bookingId);

    /**
     * Get bookings by date
     * @param date Date
     * @return List of booking responses
     */
    List<BookingResponse> getBookingsByDate(LocalDateTime date);

    /**
     * Get bookings by table number
     * @param tableNumber Table number
     * @return List of booking responses
     */
    List<BookingResponse> getBookingsByTableNumber(Integer tableNumber);

    /**
     * Get bookings by status
     * @param status Booking status
     * @return List of booking responses
     */
    List<BookingResponse> getBookingsByStatus(BookingStatus status);

    /**
     * Complete booking
     * @param bookingId Booking ID
     * @return Updated booking response
     */
    BookingResponse completeBooking(Long bookingId);
}