package org.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restaurant.dto.request.CreateUserRequest;
import org.restaurant.dto.request.LoginRequest;
import org.restaurant.dto.response.JwtAuthenticationResponse;
import org.restaurant.dto.response.SuccessResponse;
import org.restaurant.dto.response.UserResponse;
import org.restaurant.controller.AuthenticationController.ChangePasswordRequest;
import org.restaurant.controller.AuthenticationController.RefreshTokenRequest;
import org.restaurant.controller.AuthenticationController.ResetPasswordRequest;
import org.restaurant.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    private JwtAuthenticationResponse jwtResponse;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        jwtResponse = new JwtAuthenticationResponse();
        jwtResponse.setToken("mock-access-token");
        UserResponse user = new UserResponse();
        user.setId(1L);
        user.setUsername("testuser");
        jwtResponse.setUser(user);

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
    }


    @Test
    @WithMockUser(roles = {"ADMIN","MANAGER","WAITER","CHEF"})
    void getCurrentUser_ShouldReturnUser() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(userResponse);

        mockMvc.perform(get("/auth/me").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN","MANAGER","WAITER","CHEF"})
    void changePassword_ShouldReturnSuccess() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("oldpass");
        changePasswordRequest.setNewPassword("newpass");

        doNothing().when(authenticationService).changePassword(anyString(), anyString());

        mockMvc.perform(put("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void resetPassword_ShouldReturnSuccess_WhenAdmin() throws Exception {
        ResetPasswordRequest resetRequest = new ResetPasswordRequest();
        resetRequest.setNewPassword("resetpass");

        doNothing().when(authenticationService).resetPassword(anyLong(), anyString());

        mockMvc.perform(put("/auth/reset-password/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successfully"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN","MANAGER","WAITER","CHEF"})
    void logout_ShouldReturnSuccess() throws Exception {
        doNothing().when(authenticationService).logout();

        mockMvc.perform(post("/auth/logout").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN","MANAGER","WAITER","CHEF"})
    void isCurrentUserAdmin_ShouldReturnBoolean() throws Exception {
        when(authenticationService.isCurrentUserAdmin()).thenReturn(true);

        mockMvc.perform(get("/auth/is-admin").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN","MANAGER","WAITER","CHEF"})
    void isCurrentUserManager_ShouldReturnBoolean() throws Exception {
        when(authenticationService.isCurrentUserManager()).thenReturn(false);

        mockMvc.perform(get("/auth/is-manager").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN","MANAGER","WAITER","CHEF"})
    void isCurrentUserWaiter_ShouldReturnBoolean() throws Exception {
        when(authenticationService.isCurrentUserWaiter()).thenReturn(true);

        mockMvc.perform(get("/auth/is-waiter").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN","MANAGER","WAITER","CHEF"})
    void isCurrentUserChef_ShouldReturnBoolean() throws Exception {
        when(authenticationService.isCurrentUserChef()).thenReturn(false);

        mockMvc.perform(get("/auth/is-chef").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
