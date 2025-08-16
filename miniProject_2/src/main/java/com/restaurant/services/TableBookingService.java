package org.restaurant.services;

import org.restaurant.dto.request.CreateBookingRequest;
import org.restaurant.dto.request.UpdateBookingRequest;
import org.restaurant.dto.response.BookingResponse;
import org.restaurant.entities.RestaurantTable;
import org.restaurant.entities.TableBooking;
import org.restaurant.entities.User;
import org.restaurant.enums.BookingStatus;
import org.restaurant.enums.TableStatus;
import org.restaurant.exceptions.ResourceNotFoundException;
import org.restaurant.exceptions.BusinessLogicException;
import org.restaurant.repositories.TableBookingRepository;
import org.restaurant.repositories.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TableBookingService {

    @Autowired
    private TableBookingRepository bookingRepository;

    @Autowired
    private RestaurantTableRepository tableRepository;

    @Autowired
    private UserService userService;

    /**
     * Create a new table booking
     */
    public BookingResponse createBooking(CreateBookingRequest request) {
        // Validate booking time is in the future
        if (request.getBookingTime().isBefore(LocalDateTime.now())) {
            throw new BusinessLogicException("Booking time must be in the future");
        }

        // Find available table for the requested capacity
        RestaurantTable availableTable = findAvailableTable(request.getTableNumber(),
                request.getNumberOfGuests(),
                request.getBookingTime());

        if (availableTable == null) {
            throw new BusinessLogicException("No available table found for the requested time and capacity");
        }

        // Check for conflicting bookings
        LocalDateTime startTime = request.getBookingTime().minusHours(1);
        LocalDateTime endTime = request.getBookingTime().plusHours(3); // Assuming 2-hour booking duration + 1 hour buffer

        List<TableBooking> conflictingBookings = bookingRepository.findConflictingBookings(
                request.getTableNumber(), startTime, endTime, BookingStatus.RESERVED);

        if (!conflictingBookings.isEmpty()) {
            throw new BusinessLogicException("Table " + request.getTableNumber() + " is not available at the requested time");
        }

        // Create new booking
        TableBooking booking = new TableBooking();
        booking.setCustomerName(request.getCustomerName());
        booking.setBookingTime(request.getBookingTime());
        booking.setTableNumber(request.getTableNumber());
        booking.setNumberOfGuests(request.getNumberOfGuests());
        booking.setRestaurantTable(availableTable);
        booking.setStatus(BookingStatus.RESERVED);

        TableBooking savedBooking = bookingRepository.save(booking);

        // Update table status to occupied if booking is for current time
        if (isBookingTimeNow(request.getBookingTime())) {
            availableTable.setStatus(TableStatus.RESERVED);
            tableRepository.save(availableTable);
        }

        return convertToBookingResponse(savedBooking);
    }

    /**
     * Get all bookings
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        List<TableBooking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get booking by ID
     */
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {
        TableBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        return convertToBookingResponse(booking);
    }

    /**
     * Update booking
     */
    public BookingResponse updateBooking(Long bookingId, UpdateBookingRequest request) {
        TableBooking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        // Validate booking is not in the past and can be modified
        if (existingBooking.getBookingTime().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new BusinessLogicException("Cannot modify past bookings");
        }

        // Update fields if provided
        if (request.getCustomerName() != null) {
            existingBooking.setCustomerName(request.getCustomerName());
        }

        if (request.getBookingTime() != null) {
            if (request.getBookingTime().isBefore(LocalDateTime.now())) {
                throw new BusinessLogicException("Booking time must be in the future");
            }
            existingBooking.setBookingTime(request.getBookingTime());
        }

        if (request.getTableNumber() != null) {
            // Validate table availability for new table number
            validateTableAvailability(request.getTableNumber(),
                    request.getNumberOfGuests() != null ? request.getNumberOfGuests() : existingBooking.getNumberOfGuests(),
                    request.getBookingTime() != null ? request.getBookingTime() : existingBooking.getBookingTime(),
                    bookingId);
            existingBooking.setTableNumber(request.getTableNumber());
        }

        if (request.getNumberOfGuests() != null) {
            existingBooking.setNumberOfGuests(request.getNumberOfGuests());
        }

        TableBooking updatedBooking = bookingRepository.save(existingBooking);
        return convertToBookingResponse(updatedBooking);
    }

    /**
     * Cancel booking (soft delete by changing status)
     */
    public void cancelBooking(Long bookingId) {
        TableBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessLogicException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Free up the table if it was occupied by this booking
        if (booking.getRestaurantTable() != null &&
                booking.getRestaurantTable().getStatus() == TableStatus.OCCUPIED) {
            booking.getRestaurantTable().setStatus(TableStatus.AVAILABLE);
            tableRepository.save(booking.getRestaurantTable());
        }
    }

    /**
     * Delete booking permanently
     */
    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new ResourceNotFoundException("Booking not found with ID: " + bookingId);
        }
        bookingRepository.deleteById(bookingId);
    }

    /**
     * Get bookings by date
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByDate(LocalDateTime date) {
        List<TableBooking> bookings = bookingRepository.findByBookingDate(date);
        return bookings.stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get bookings by table number
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByTableNumber(Integer tableNumber) {
        List<TableBooking> bookings = bookingRepository.findByTableNumber(tableNumber);
        return bookings.stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get bookings by status
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByStatus(BookingStatus status) {
        List<TableBooking> bookings = bookingRepository.findByStatus(status);
        return bookings.stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mark booking as completed
     */
    public BookingResponse completeBooking(Long bookingId) {
        TableBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        booking.setStatus(BookingStatus.COMPLETED);
        TableBooking updatedBooking = bookingRepository.save(booking);

        // Free up the table
        if (booking.getRestaurantTable() != null) {
            booking.getRestaurantTable().setStatus(TableStatus.AVAILABLE);
            tableRepository.save(booking.getRestaurantTable());
        }

        return convertToBookingResponse(updatedBooking);
    }

    // Helper methods

    private RestaurantTable findAvailableTable(Integer requestedTableNumber, Integer numberOfGuests, LocalDateTime bookingTime) {
        // First, try to find the specific table if requested
        if (requestedTableNumber != null) {
            Optional<RestaurantTable> requestedTable = tableRepository.findByTableNumber(Long.valueOf(requestedTableNumber));
            if (requestedTable.isPresent() &&
                    requestedTable.get().getCapacity() >= numberOfGuests &&
                    requestedTable.get().getStatus() == TableStatus.AVAILABLE) {
                return requestedTable.get();
            }
        }

        // Find smallest available table that can accommodate the guests
        Optional<RestaurantTable> availableTable = tableRepository.findSmallestAvailableTable(numberOfGuests);
        return availableTable.orElse(null);
    }

    private boolean isBookingTimeNow(LocalDateTime bookingTime) {
        LocalDateTime now = LocalDateTime.now();
        return bookingTime.isBefore(now.plusMinutes(30)) && bookingTime.isAfter(now.minusMinutes(30));
    }

    private void validateTableAvailability(Integer tableNumber, Integer numberOfGuests,
                                           LocalDateTime bookingTime, Long excludeBookingId) {
        LocalDateTime startTime = bookingTime.minusHours(1);
        LocalDateTime endTime = bookingTime.plusHours(3);

        List<TableBooking> conflictingBookings = bookingRepository.findConflictingBookings(
                tableNumber, startTime, endTime, BookingStatus.RESERVED);

        // Remove the current booking from conflicts (for updates)
        conflictingBookings.removeIf(booking -> booking.getBookingId().equals(excludeBookingId));

        if (!conflictingBookings.isEmpty()) {
            throw new BusinessLogicException("Table " + tableNumber + " is not available at the requested time");
        }
    }

    private BookingResponse convertToBookingResponse(TableBooking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getBookingId());
        response.setCustomerName(booking.getCustomerName());
        response.setBookingTime(booking.getBookingTime());
        response.setTableNumber(booking.getTableNumber());
        response.setNumberOfGuests(booking.getNumberOfGuests());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        return response;
    }
}