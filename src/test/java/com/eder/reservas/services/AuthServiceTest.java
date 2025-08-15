package com.eder.reservas.services;

import com.eder.reservas.domain.user.User;
import com.eder.reservas.domain.user.UserRole;
import com.eder.reservas.dtos.auth.LoginDTO;
import com.eder.reservas.dtos.auth.RegisterDTO;
import com.eder.reservas.dtos.auth.ResponseDTO;
import com.eder.reservas.exceptions.ApiException;
import com.eder.reservas.infra.security.TokenService;
import com.eder.reservas.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    public void shouldRegisterNewUserIfEmailIsAvailable() {
        RegisterDTO newUser = new RegisterDTO(
            "name",
            "test@email.com",
            "password",
            UserRole.USER
        );

        User savedUser = new User();
        savedUser.setName(newUser.name());
        savedUser.setEmail(newUser.email());
        savedUser.setPassword(newUser.password());
        savedUser.setRole(newUser.role());

        String expectedToken = "test token";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenService.generateToken(any(User.class))).thenReturn(expectedToken);

        ResponseDTO response = authService.register(newUser);

        assertNotNull(response);
        assertEquals("test@email.com", response.email());
        assertEquals(expectedToken, response.token());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenService, times(1)).generateToken(any(User.class));
    }

    @Test
    public void shouldNotRegisterNewUserIfEmailIsNotAvailable() {
        RegisterDTO newUser = new RegisterDTO(
                "name",
                "test@email.com",
                "password",
                UserRole.USER
        );

        User existingUser = new User();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));

        ApiException exception = assertThrows(ApiException.class, () -> {
            authService.register(newUser);
        });
        assertEquals("Email already used", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(tokenService, never()).generateToken(any(User.class));
    }

    @Test
    public void shouldLoginUserIfPasswordMatches() {
        LoginDTO user = new LoginDTO(
                "test@email.com",
                "test password"
        );

        User existingUser = new User();
        existingUser.setName("name");
        existingUser.setEmail("test@email.com");
        existingUser.setPassword("test password");
        existingUser.setRole(UserRole.USER);

        String expectedToken = "test token";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(tokenService.generateToken(any(User.class))).thenReturn(expectedToken);

        ResponseDTO response = authService.login(user);

        assertNotNull(response);
        assertEquals(expectedToken, response.token());
        assertEquals(user.email(), response.email());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(tokenService, times(1)).generateToken(any(User.class));
    }

    @Test
    public void shouldNotLoginIfUserDoesNotExist() {
        LoginDTO user = new LoginDTO(
                "test@email.com",
                "test password"
        );

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            authService.login(user);
        });
        assertEquals("No user found for this email", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenService, never()).generateToken(any(User.class));
    }

    @Test
    public void shouldNotLoginUserIfPasswordsDoNotMatch() {
        LoginDTO user = new LoginDTO(
                "test@email.com",
                "wrong test password"
        );

        User existingUser = new User();
        existingUser.setName("name");
        existingUser.setEmail("test@email.com");
        existingUser.setPassword("test password");
        existingUser.setRole(UserRole.USER);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class, () -> {
            authService.login(user);
        });
        assertEquals("Invalid password", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(tokenService, never()).generateToken(any(User.class));
    }
}
