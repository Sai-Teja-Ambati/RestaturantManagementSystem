package org.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restaurant.entities.RestaurantTable;
import org.restaurant.enums.TableStatus;
import org.restaurant.service.RestaurantTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantTableController.class)
class RestaurantTableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestaurantTableService tableService;

    private RestaurantTable table;

    @BeforeEach
    void setUp() {
        table = new RestaurantTable();
        table.setTableId(1L);
        table.setCapacity(4);
        table.setStatus(TableStatus.AVAILABLE);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER"})
    void createTable_ShouldReturnCreated() throws Exception {
        RestaurantTableController.CreateTableRequest createRequest = new RestaurantTableController.CreateTableRequest();
        createRequest.setCapacity(4);
        when(tableService.createTable(anyInt())).thenReturn(table);

        mockMvc.perform(post("/tables")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tableId").value(1L));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER", "WAITER"})
    void getAllTables_ShouldReturnList() throws Exception {
        when(tableService.getAllTables()).thenReturn(Collections.singletonList(table));

        mockMvc.perform(get("/tables").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTable_ShouldReturnSuccess() throws Exception {
        doNothing().when(tableService).deleteTable(anyLong());

        mockMvc.perform(delete("/tables/1").with(csrf()))
                .andExpect(status().isOk());
    }
}
