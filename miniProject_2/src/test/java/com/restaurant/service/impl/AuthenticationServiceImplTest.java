package org.restaurant.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.restaurant.dto.request.CreateUserRequest;
import org.restaurant.dto.request.LoginRequest;
import org.restaurant.dto.response.JwtAuthenticationResponse;
import org.restaurant.dto.response.UserResponse;
import org.restaurant.entities.User;
import org.restaurant.enums.UserRole;
import org.restaurant.exceptions.BusinessLogicException;
import org.restaurant.exceptions.ResourceAlreadyExistsException;
import org.restaurant.repository.UserRepository;
import org.restaurant.security.JwtUtils;
import org.restaurant.service.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    @InjectMocks AuthenticationServiceImpl service;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtUtils jwtUtils;
    @Mock CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user1");
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void register_UsernameExists_Throws() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("foo"); req.setEmail("bar@x.com");
        when(userRepository.existsByUsername("foo")).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> service.register(req));
    }

    @Test
    void register_EmailExists_Throws() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("foo2"); req.setEmail("bar2@x.com");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("bar2@x.com")).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> service.register(req));
    }

    @Test
    void register_PrivilegedRole_Throws() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("foo3"); req.setEmail("bar3@x.com");
        req.setRole(UserRole.ADMIN);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        assertThrows(BusinessLogicException.class, () -> service.register(req));
    }

    @Test
    void registerAdmin_NullRole_Throws() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("foo"); req.setEmail("e@x.com");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        req.setRole(null);
        assertThrows(BusinessLogicException.class, () -> service.registerAdmin(req));
    }

    @Test
    void registerAdmin_NonPrivilegedRole_Throws() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("foo"); req.setEmail("e@x.com"); req.setRole(UserRole.WAITER);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        assertThrows(BusinessLogicException.class, () -> service.registerAdmin(req));
    }

    @Test
    void login_InvalidCredentials_Throws() {
        LoginRequest req = new LoginRequest();
        req.setUsername("usr"); req.setPassword("bad");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));
        assertThrows(BusinessLogicException.class, () -> service.login(req));
    }

    @Test
    void refreshToken_InvalidToken_Throws() {
        when(jwtUtils.validateJwtToken("invalidtoken")).thenReturn(false);
        assertThrows(BusinessLogicException.class, () -> service.refreshToken("invalidtoken"));
    }

    @Test
    void changePassword_WrongCurrent_Throws() {
        User user = new User();
        user.setPassword("encoded");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        assertThrows(BusinessLogicException.class, () -> service.changePassword("wrong", "new"));
    }

    @Test
    void resetPassword_UserNotFound_Throws() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(BusinessLogicException.class, () -> service.resetPassword(5L, "newpass"));
    }

    @Test
    void getCurrentUser_NotFound_Throws() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(BusinessLogicException.class, () -> service.getCurrentUser());
    }

    @Test
    void currentUserHasRole_FalseIfUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertFalse(service.currentUserHasRole(UserRole.ADMIN));
    }

    @Test
    void logout_ClearsSecurityContext() {
        service.logout();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void validateUserPermission_NonAdminWrongRole_Throws() {
        UserResponse ur = new UserResponse();
        ur.setRole(UserRole.WAITER);
        AuthenticationServiceImpl spyService = spy(service);
        doReturn(ur).when(spyService).getCurrentUser();
        assertThrows(BusinessLogicException.class, () -> spyService.validateUserPermission(UserRole.MANAGER));
    }
}
