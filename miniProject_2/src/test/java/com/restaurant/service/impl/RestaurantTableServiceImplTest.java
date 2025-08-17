package org.restaurant.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.restaurant.entities.RestaurantTable;
import org.restaurant.enums.TableStatus;
import org.restaurant.exceptions.BusinessLogicException;
import org.restaurant.exceptions.ResourceNotFoundException;
import org.restaurant.repository.RestaurantTableRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantTableServiceImplTest {

    @InjectMocks
    private RestaurantTableServiceImpl tableService;
    @Mock private RestaurantTableRepository tableRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTable_CapacityLessThanOne_Throws() {
        assertThrows(BusinessLogicException.class, () -> tableService.createTable(0));
    }

    @Test
    void getTableById_NotFound_Throws() {
        when(tableRepository.findById(11L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tableService.getTableById(11L));
    }

    @Test
    void markTableAsOccupied_AlreadyOccupied_Throws() {
        RestaurantTable tab = new RestaurantTable(); tab.setStatus(TableStatus.OCCUPIED);
        when(tableRepository.findById(2L)).thenReturn(Optional.of(tab));
        assertThrows(BusinessLogicException.class, () -> tableService.markTableAsOccupied(2L));
    }

    @Test
    void markTableAsReserved_IfOccupied_Throws() {
        RestaurantTable tab = new RestaurantTable(); tab.setStatus(TableStatus.OCCUPIED);
        when(tableRepository.findById(2L)).thenReturn(Optional.of(tab));
        assertThrows(BusinessLogicException.class, () -> tableService.markTableAsReserved(2L));
    }

    @Test
    void deleteTable_IfOccupied_Throws() {
        RestaurantTable tab = new RestaurantTable(); tab.setStatus(TableStatus.OCCUPIED);
        when(tableRepository.findById(4L)).thenReturn(Optional.of(tab));
        assertThrows(BusinessLogicException.class, () -> tableService.deleteTable(4L));
    }
}
