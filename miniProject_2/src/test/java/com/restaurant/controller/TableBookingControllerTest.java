package org.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restaurant.dto.request.CreateBookingRequest;
import org.restaurant.dto.request.UpdateBookingRequest;
import org.restaurant.dto.response.BookingResponse;
import org.restaurant.dto.response.SuccessResponse;
import org.restaurant.enums.BookingStatus;
import org.restaurant.service.TableBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableBookingController.class)
class TableBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TableBookingService bookingService;

    private BookingResponse bookingResponse;

    @BeforeEach
    void setUp() {
        bookingResponse = new BookingResponse();
        bookingResponse.setId(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER", "WAITER"})
    void getAllBookings_ShouldReturnList() throws Exception {
        when(bookingService.getAllBookings()).thenReturn(Collections.singletonList(bookingResponse));

        mockMvc.perform(get("/bookings").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER"})
    void cancelBooking_ShouldReturnSuccess() throws Exception {
        doNothing().when(bookingService).cancelBooking(anyLong());

        mockMvc.perform(delete("/bookings/1").with(csrf()))
                .andExpect(status().isOk());
    }
}
