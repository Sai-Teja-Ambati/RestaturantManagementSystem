package org.restaurant.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.restaurant.entities.User;
import org.restaurant.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceImplTest {

    @InjectMocks CustomUserDetailsServiceImpl service;
    @Mock UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_NotFound_Throws() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("foo"));
    }

    @Test
    void loadUserById_NotFound_Throws() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserById(123L));
    }

    @Test
    void existsByUsername_ReturnsTrue() {
        when(userRepository.existsByUsername("aaa")).thenReturn(true);
        assertTrue(service.existsByUsername("aaa"));
    }

    @Test
    void existsByEmail_ReturnsFalse() {
        when(userRepository.existsByEmail("mail")).thenReturn(false);
        assertFalse(service.existsByEmail("mail"));
    }

    @Test
    void findByEmail_NotFound_Throws() {
        when(userRepository.findByEmail("mail")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.findByEmail("mail"));
    }

    @Test
    void findByUsername_NotFound_Throws() {
        when(userRepository.findByUsername("userx")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.findByUsername("userx"));
    }
}
