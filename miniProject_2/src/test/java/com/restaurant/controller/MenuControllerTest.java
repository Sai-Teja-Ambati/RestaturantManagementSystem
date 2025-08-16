package org.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.restaurant.entities.MenuItem;
import org.restaurant.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MenuController.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuItemService menuItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private MenuItem menuItem;
    private MenuController.CreateMenuItemRequest createRequest;
    private MenuController.UpdateMenuItemRequest updateRequest;
    private List<MenuItem> menuItems;
    private List<String> categories;

    @BeforeEach
    void setUp() {
        menuItem = new MenuItem();
        menuItem.setItemId(1L);
        menuItem.setMenuItemName("Paneer Tikka");
        menuItem.setCategory("Veg Starters");
        menuItem.setPrice(new BigDecimal("6.99"));

        createRequest = new MenuController.CreateMenuItemRequest();
        createRequest.setName("Paneer Tikka");
        createRequest.setCategory("Veg Starters");
        createRequest.setPrice(new BigDecimal("6.99"));

        updateRequest = new MenuController.UpdateMenuItemRequest();
        updateRequest.setName("Updated Paneer Tikka");
        updateRequest.setCategory("Veg Starters");
        updateRequest.setPrice(new BigDecimal("7.99"));

        MenuItem secondItem = new MenuItem();
        secondItem.setItemId(2L);
        secondItem.setMenuItemName("Chicken Tikka");
        secondItem.setCategory("Non-Veg Starters");
        secondItem.setPrice(new BigDecimal("7.99"));

        menuItems = Arrays.asList(menuItem, secondItem);

        categories = Arrays.asList("Veg Starters", "Non-Veg Starters", "Indian Breads");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMenuItem_ShouldReturnCreated_WhenAdminRole() throws Exception {
        when(menuItemService.createMenuItem(anyString(), anyString(), any(BigDecimal.class))).thenReturn(menuItem);

        mockMvc.perform(post("/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(1L))
                .andExpect(jsonPath("$.menuItemName").value("Paneer Tikka"))
                .andExpect(jsonPath("$.category").value("Veg Starters"))
                .andExpect(jsonPath("$.price").value(6.99));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createMenuItem_ShouldReturnCreated_WhenManagerRole() throws Exception {
        when(menuItemService.createMenuItem(anyString(), anyString(), any(BigDecimal.class))).thenReturn(menuItem);

        mockMvc.perform(post("/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMenuItemById_ShouldReturnMenuItem_WhenExists() throws Exception {
        when(menuItemService.getMenuItemById(1L)).thenReturn(menuItem);

        mockMvc.perform(get("/menu/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1L))
                .andExpect(jsonPath("$.menuItemName").value("Paneer Tikka"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateMenuItem_ShouldReturnUpdated_WhenManagerRole() throws Exception {
        MenuItem updated = new MenuItem();
        updated.setItemId(1L);
        updated.setMenuItemName("Updated Paneer Tikka");
        updated.setCategory("Veg Starters");
        updated.setPrice(new BigDecimal("7.99"));

        when(menuItemService.updateMenuItem(eq(1L), anyString(), anyString(), any(BigDecimal.class))).thenReturn(updated);

        mockMvc.perform(put("/menu/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1L))
                .andExpect(jsonPath("$.menuItemName").value("Updated Paneer Tikka"))
                .andExpect(jsonPath("$.price").value(7.99));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void searchMenuItems_ShouldReturnFilteredByName() throws Exception {
        when(menuItemService.searchMenuItems(eq("Paneer"), isNull())).thenReturn(Arrays.asList(menuItem));

        mockMvc.perform(get("/menu/search")
                        .param("name", "Paneer")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].menuItemName").value("Paneer Tikka"));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void searchMenuItems_ShouldReturnFilteredByCategory() throws Exception {
        when(menuItemService.searchMenuItems(isNull(), eq("Veg Starters"))).thenReturn(Arrays.asList(menuItem));

        mockMvc.perform(get("/menu/search")
                        .param("category", "Veg Starters")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("Veg Starters"));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void searchMenuItems_ShouldReturnAll_WhenNoParams() throws Exception {
        when(menuItemService.getAllMenuItems()).thenReturn(menuItems);

        mockMvc.perform(get("/menu/search").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(menuItems.size()));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void getMenuItemsUnderPrice_ShouldReturnFiltered() throws Exception {
        when(menuItemService.getMenuItemsUnderPrice(new BigDecimal("7.00"))).thenReturn(Arrays.asList(menuItem));

        mockMvc.perform(get("/menu/under-price/7.00").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].price").value(6.99));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMenuItemsByCategoryOrdered_ShouldReturnFiltered() throws Exception {
        when(menuItemService.getMenuItemsByCategoryOrderedByPrice("Veg Starters"))
                .thenReturn(Arrays.asList(menuItem));

        mockMvc.perform(get("/menu/category/Veg Starters/ordered").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("Veg Starters"));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void menuItemExists_ShouldReturnTrue() throws Exception {
        when(menuItemService.menuItemExists(1L)).thenReturn(true);

        mockMvc.perform(get("/menu/1/exists").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void menuItemExists_ShouldReturnFalse() throws Exception {
        when(menuItemService.menuItemExists(999L)).thenReturn(false);

        mockMvc.perform(get("/menu/999/exists").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
