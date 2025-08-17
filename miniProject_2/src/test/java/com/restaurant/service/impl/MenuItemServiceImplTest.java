package org.restaurant.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.restaurant.entities.MenuItem;
import org.restaurant.exceptions.ResourceAlreadyExistsException;
import org.restaurant.exceptions.ResourceNotFoundException;
import org.restaurant.repository.MenuItemRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuItemServiceImplTest {

    @InjectMocks MenuItemServiceImpl service;
    @Mock MenuItemRepository menuItemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMenuItem_NameExists_Throws() {
        when(menuItemRepository.existsByMenuItemName("Pizza")).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class,
                () -> service.createMenuItem("Pizza", "Italian", BigDecimal.TEN));
    }

    @Test
    void getMenuItemById_NotFound_Throws() {
        when(menuItemRepository.findById(4L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getMenuItemById(4L));
    }

    @Test
    void updateMenuItem_ItemNotFound_Throws() {
        when(menuItemRepository.findById(8L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateMenuItem(8L, "name", "cat", BigDecimal.ONE));
    }

    @Test
    void updateMenuItem_NameConflict_Throws() {
        MenuItem item = new MenuItem(); item.setMenuItemName("Burger");
        when(menuItemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(menuItemRepository.existsByMenuItemName("Pizza")).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class,
                () -> service.updateMenuItem(10L, "Pizza", "FastFood", BigDecimal.ONE));
    }

    @Test
    void deleteMenuItem_NotExists_Throws() {
        when(menuItemRepository.existsById(17L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.deleteMenuItem(17L));
    }

    @Test
    void menuItemExists_ReturnsTrue() {
        when(menuItemRepository.existsById(22L)).thenReturn(true);
        assertTrue(service.menuItemExists(22L));
    }

    @Test
    void menuItemExistsByName_ReturnsFalse() {
        when(menuItemRepository.existsByMenuItemName("Chicken")).thenReturn(false);
        assertFalse(service.menuItemExistsByName("Chicken"));
    }

    @Test
    void getMenuItemByName_NotFound_Throws() {
        when(menuItemRepository.findByMenuItemName("Soup")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getMenuItemByName("Soup"));
    }
}
