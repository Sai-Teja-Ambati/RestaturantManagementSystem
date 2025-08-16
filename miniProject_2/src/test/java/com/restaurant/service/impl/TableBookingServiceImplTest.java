package org.restaurant.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.restaurant.dto.request.CreateBookingRequest;
import org.restaurant.dto.request.UpdateBookingRequest;
import org.restaurant.entities.RestaurantTable;
import org.restaurant.entities.TableBooking;
import org.restaurant.enums.BookingStatus;
import org.restaurant.enums.TableStatus;
import org.restaurant.exceptions.BusinessLogicException;
import org.restaurant.exceptions.ResourceNotFoundException;
import org.restaurant.repository.RestaurantTableRepository;
import org.restaurant.repository.TableBookingRepository;
import org.restaurant.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TableBookingServiceImplTest {

    @InjectMocks
    private TableBookingServiceImpl bookingService;
    @Mock private TableBookingRepository bookingRepository;
    @Mock private RestaurantTableRepository tableRepository;
    @Mock private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBooking_PastTime_Throws() {
        CreateBookingRequest req = new CreateBookingRequest();
        req.setBookingTime(LocalDateTime.now().minusHours(2));
        assertThrows(BusinessLogicException.class, () -> bookingService.createBooking(req));
    }

    @Test
    void getBookingById_NotFound_Throws() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(1L));
    }

    @Test
    void updateBooking_PastBookingTime_Throws() {
        TableBooking b = new TableBooking(); b.setBookingTime(LocalDateTime.now().minusHours(3));
        when(bookingRepository.findById(6L)).thenReturn(Optional.of(b));
        UpdateBookingRequest r = new UpdateBookingRequest();
        assertThrows(BusinessLogicException.class, () -> bookingService.updateBooking(6L, r));
    }

    @Test
    void cancelBooking_AlreadyCancelled_Throws() {
        TableBooking b = new TableBooking(); b.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById(9L)).thenReturn(Optional.of(b));
        assertThrows(BusinessLogicException.class, () -> bookingService.cancelBooking(9L));
    }

    @Test
    void deleteBooking_NotFound_Throws() {
        when(bookingRepository.existsById(10L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> bookingService.deleteBooking(10L));
    }
}
