package org.restaurant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restaurant.dto.request.CreateBookingRequest;
import org.restaurant.dto.request.UpdateBookingRequest;
import org.restaurant.dto.response.BookingResponse;
import org.restaurant.dto.response.SuccessResponse;
import org.restaurant.enums.BookingStatus;
import org.restaurant.service.TableBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class TableBookingController {

    private static final Logger logger = LoggerFactory.getLogger(TableBookingController.class);

    @Autowired
    private TableBookingService bookingService;

    /**
     * Create a new table booking
     * POST /bookings
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        logger.info("Creating new booking for table: {} with {} guests", request.getTableNumber(), request.getNumberOfGuests());
        BookingResponse booking = bookingService.createBooking(request);
        logger.info("Booking created successfully with ID: {}", booking.getId());
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    /**
     * Get all bookings
     * GET /bookings
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        logger.info("Fetching all bookings");
        List<BookingResponse> bookings = bookingService.getAllBookings();
        logger.info("Retrieved {} bookings", bookings.size());
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get booking by ID
     * GET /bookings/{id}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        logger.info("Fetching booking with ID: {}", id);
        BookingResponse booking = bookingService.getBookingById(id);
        logger.info("Booking details: {}", booking);
        return ResponseEntity.ok(booking);
    }

    /**
     * Update booking by ID
     * PUT /bookings/{id}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<BookingResponse> updateBooking(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateBookingRequest request) {
        logger.info("Updating booking with ID: {}", id);
        BookingResponse updatedBooking = bookingService.updateBooking(id, request);
        logger.info("Booking updated successfully: {}", updatedBooking);
        return ResponseEntity.ok(updatedBooking);
    }

    /**
     * Cancel booking (soft delete)
     * DELETE /bookings/{id}
     * Required: ADMIN or MANAGER role
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SuccessResponse> cancelBooking(@PathVariable Long id) {
        logger.info("Cancelling booking with ID: {}", id);
        bookingService.cancelBooking(id);
        logger.info("Booking cancelled successfully");
        return ResponseEntity.ok(new SuccessResponse("Booking cancelled successfully"));
    }

    /**
     * Delete booking permanently
     * DELETE /bookings/{id}/permanent
     * Required: ADMIN role only
     */
    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> deleteBookingPermanently(@PathVariable Long id) {
        logger.info("Permanently deleting booking with ID: {}", id);
        bookingService.deleteBooking(id);
        logger.info("Booking deleted permanently");
        return ResponseEntity.ok(new SuccessResponse("Booking deleted permanently"));
    }

    /**
     * Get bookings by date
     * GET /bookings/date/{date}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<BookingResponse>> getBookingsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        logger.info("Fetching bookings for date: {}", date);
        List<BookingResponse> bookings = bookingService.getBookingsByDate(date);
        logger.info("Retrieved {} bookings for the date {}", bookings.size(), date);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get bookings by table number
     * GET /bookings/table/{tableNumber}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/table/{tableNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<BookingResponse>> getBookingsByTableNumber(@PathVariable Integer tableNumber) {
        logger.info("Fetching bookings for table number: {}", tableNumber);
        List<BookingResponse> bookings = bookingService.getBookingsByTableNumber(tableNumber);
        logger.info("Retrieved {} bookings for table number {}", bookings.size(), tableNumber);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get bookings by status
     * GET /bookings/status/{status}
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<BookingResponse>> getBookingsByStatus(@PathVariable BookingStatus status) {
        logger.info("Fetching bookings with status: {}", status);
        List<BookingResponse> bookings = bookingService.getBookingsByStatus(status);
        logger.info("Retrieved {} bookings with status {}", bookings.size(), status);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Mark booking as completed
     * PUT /bookings/{id}/complete
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<BookingResponse> completeBooking(@PathVariable Long id) {
        logger.info("Marking booking as completed with ID: {}", id);
        BookingResponse completedBooking = bookingService.completeBooking(id);
        logger.info("Booking marked as completed: {}", completedBooking);
        return ResponseEntity.ok(completedBooking);
    }

    /**
     * Get today's bookings
     * GET /bookings/today
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<BookingResponse>> getTodaysBookings() {
        logger.info("Fetching today's bookings");
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<BookingResponse> bookings = bookingService.getBookingsByDate(today);
        logger.info("Retrieved {} bookings for today", bookings.size());
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get active bookings (RESERVED status)
     * GET /bookings/active
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<BookingResponse>> getActiveBookings() {
        logger.info("Fetching active bookings (RESERVED status)");
        List<BookingResponse> bookings = bookingService.getBookingsByStatus(BookingStatus.RESERVED);
        logger.info("Retrieved {} active bookings", bookings.size());
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get upcoming bookings (next 24 hours)
     * GET /bookings/upcoming
     * Required: ADMIN, MANAGER, or WAITER role
     */
    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAITER')")
    public ResponseEntity<List<BookingResponse>> getUpcomingBookings() {
        logger.info("Fetching upcoming bookings (next 24 hours)");
        // This would need a custom method in service for date range queries
        // For now, return today's bookings as a placeholder
        List<BookingResponse> bookings = bookingService.getBookingsByStatus(BookingStatus.RESERVED);
        logger.info("Retrieved {} upcoming bookings", bookings.size());
        return ResponseEntity.ok(bookings);
    }
}