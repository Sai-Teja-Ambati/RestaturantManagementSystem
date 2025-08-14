package com.restaurant.service.impl;

import com.restaurant.entity.TableBooking;
import com.restaurant.enums.ReservationStatus;
import com.restaurant.repository.TableBookingRepository;
import com.restaurant.service.TableBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class TableBookingServiceImpl implements TableBookingService {

    @Autowired
    private TableBookingRepository bookingRepository;

    @Override
    public TableBooking createBooking(String customerName, LocalDateTime bookingTime, Integer tableNumber, Integer numberOfGuests) {
        return createBooking(customerName, bookingTime, tableNumber, numberOfGuests, null);
    }

    @Override
    public TableBooking createBooking(String customerName, LocalDateTime bookingTime, Integer tableNumber, Integer numberOfGuests, String notes) {
        log.info("Creating booking for customer: {} at table: {} for {} guests", customerName, tableNumber, numberOfGuests);
        
        // Check if table is available at the requested time
        if (!isTableAvailable(tableNumber, bookingTime)) {
            throw new RuntimeException("Table " + tableNumber + " is not available at the requested time");
        }
        
        TableBooking booking = TableBooking.builder()
                .customerName(customerName)
                .bookingTime(bookingTime)
                .tableNumber(tableNumber)
                .numberOfGuests(numberOfGuests)
                .notes(notes)
                .status(ReservationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        
        TableBooking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with ID: {}", savedBooking.getId());
        return savedBooking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableBooking> getAllBookings() {
        log.debug("Fetching all bookings");
        return bookingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TableBooking> getBookingById(Long id) {
        log.debug("Fetching booking with ID: {}", id);
        return bookingRepository.findById(id);
    }

    @Override
    public TableBooking updateBooking(Long id, String customerName, LocalDateTime bookingTime, Integer tableNumber, Integer numberOfGuests, String notes) {
        log.info("Updating booking with ID: {}", id);
        
        TableBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));
        
        // If table number or booking time is changing, check availability
        if (!booking.getTableNumber().equals(tableNumber) || !booking.getBookingTime().equals(bookingTime)) {
            if (!isTableAvailable(tableNumber, bookingTime)) {
                throw new RuntimeException("Table " + tableNumber + " is not available at the requested time");
            }
        }
        
        booking.setCustomerName(customerName);
        booking.setBookingTime(bookingTime);
        booking.setTableNumber(tableNumber);
        booking.setNumberOfGuests(numberOfGuests);
        booking.setNotes(notes);
        
        TableBooking updatedBooking = bookingRepository.save(booking);
        log.info("Booking updated successfully with ID: {}", updatedBooking.getId());
        return updatedBooking;
    }

    @Override
    public void cancelBooking(Long id) {
        log.info("Cancelling booking with ID: {}", id);
        
        TableBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));
        
        booking.setStatus(ReservationStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Booking cancelled successfully with ID: {}", id);
    }

    @Override
    public void deleteBooking(Long id) {
        log.info("Deleting booking with ID: {}", id);
        
        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found with ID: " + id);
        }
        
        bookingRepository.deleteById(id);
        log.info("Booking deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableBooking> getBookingsByStatus(ReservationStatus status) {
        log.debug("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableBooking> getBookingsByTableNumber(Integer tableNumber) {
        log.debug("Fetching bookings for table number: {}", tableNumber);
        return bookingRepository.findByTableNumber(tableNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableBooking> getBookingsByCustomerName(String customerName) {
        log.debug("Fetching bookings for customer: {}", customerName);
        return bookingRepository.findByCustomerName(customerName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableBooking> getBookingsByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("Fetching bookings between {} and {}", startTime, endTime);
        return bookingRepository.findByBookingTimeBetween(startTime, endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTableAvailable(Integer tableNumber, LocalDateTime bookingTime) {
        log.debug("Checking availability for table {} at {}", tableNumber, bookingTime);
        
        // Check for conflicting bookings (within 2 hours window)
        LocalDateTime startWindow = bookingTime.minusHours(2);
        LocalDateTime endWindow = bookingTime.plusHours(2);
        
        List<TableBooking> conflictingBookings = bookingRepository.findConflictingBookings(
                tableNumber, startWindow, endWindow);
        
        boolean isAvailable = conflictingBookings.isEmpty();
        log.debug("Table {} availability at {}: {}", tableNumber, bookingTime, isAvailable);
        return isAvailable;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableBooking> getActiveBookings() {
        log.debug("Fetching active bookings");
        return bookingRepository.findByStatus(ReservationStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public long getBookingCount() {
        return bookingRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveBookingCount() {
        return bookingRepository.countByStatus(ReservationStatus.ACTIVE);
    }
}