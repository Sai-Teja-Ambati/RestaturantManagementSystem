package org.restaurant.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.restaurant.dto.request.CreateUserRequest;
import org.restaurant.entities.User;
import org.restaurant.enums.UserRole;
import org.restaurant.exceptions.ResourceAlreadyExistsException;
import org.restaurant.exceptions.ResourceNotFoundException;
import org.restaurant.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_UsernameExists_Throws() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("abc"); req.setEmail("a@b.com");
        when(userRepository.existsByUsername("abc")).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(req));
    }

    @Test
    void createUser_EmailExists_Throws() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("unique"); req.setEmail("exists@x.com");
        when(userRepository.existsByUsername("unique")).thenReturn(false);
        when(userRepository.existsByEmail("exists@x.com")).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(req));
    }

    @Test
    void getUserById_NotFound_Throws() {
        when(userRepository.findById(29L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(29L));
    }

    @Test
    void getUserByUsername_NotFound_Throws() {
        when(userRepository.findByUsername("foo")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername("foo"));
    }

    @Test
    void updateUser_UsernameTaken_Throws() {
        User orig = new User(); orig.setUserId(32L); orig.setUsername("old"); orig.setEmail("old@e.com");
        CreateUserRequest req = new CreateUserRequest(); req.setUsername("taken"); req.setEmail("old@e.com");
        when(userRepository.findById(32L)).thenReturn(Optional.of(orig));
        when(userRepository.existsByUsername("taken")).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(32L, req));
    }

    @Test
    void updateUser_EmailTaken_Throws() {
        User orig = new User(); orig.setUserId(35L); orig.setUsername("old"); orig.setEmail("old@e.com");
        CreateUserRequest req = new CreateUserRequest(); req.setUsername("old"); req.setEmail("new@e.com");
        when(userRepository.findById(35L)).thenReturn(Optional.of(orig));
        when(userRepository.existsByEmail("new@e.com")).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(35L, req));
    }

    @Test
    void deleteUser_NotExists_Throws() {
        when(userRepository.existsById(100L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(100L));
    }
}
