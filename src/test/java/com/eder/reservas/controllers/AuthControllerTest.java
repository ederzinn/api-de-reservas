package com.eder.reservas.controllers;

import com.eder.reservas.domain.user.UserRole;
import com.eder.reservas.dtos.auth.LoginDTO;
import com.eder.reservas.dtos.auth.RegisterDTO;
import com.eder.reservas.dtos.auth.ResponseDTO;
import com.eder.reservas.exceptions.ApiException;
import com.eder.reservas.infra.security.SecurityConfig;
import com.eder.reservas.infra.security.TokenService;
import com.eder.reservas.repositories.UserRepository;
import com.eder.reservas.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TokenService tokenService;

    @Test
    public void shouldRegisterUserAndReturn201() throws Exception {
        RegisterDTO data = new RegisterDTO(
                "name",
                "test@email.com",
                "password",
                UserRole.USER
        );

        ResponseDTO expectedResponse = new ResponseDTO(
                "expected token",
                "test@email.com"
        );

        when(authService.register(any(RegisterDTO.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("expected token"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    public void shouldNotRegisterUserAndReturn409() throws Exception {
        RegisterDTO data = new RegisterDTO(
                "name",
                "test@email.com",
                "password",
                UserRole.USER
        );

        ApiException exception = new ApiException("Email already used", HttpStatus.CONFLICT);

        when(authService.register(any(RegisterDTO.class))).thenThrow(exception);

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already used"));
    }

    @Test
    public void shouldLoginUserAndReturn200() throws Exception {
        LoginDTO data = new LoginDTO(
                "test@email.com",
                "password"
        );

        ResponseDTO expectedResponse = new ResponseDTO(
                "expected token",
                "test@email.com"
        );

        when(authService.login(any(LoginDTO.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("expected token"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    public void shouldNotLoginUserAndThrow404() throws Exception {
        LoginDTO data = new LoginDTO(
                "test@email.com",
                "password"
        );

        ApiException exception = new ApiException("No user found for this email", HttpStatus.NOT_FOUND);

        when(authService.login(any(LoginDTO.class))).thenThrow(exception);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No user found for this email"));
    }

    @Test
    public void shouldNotLoginUserAndThrow401() throws Exception {
        LoginDTO data = new LoginDTO(
                "test@email.com",
                "password"
        );

        ApiException exception = new ApiException("Invalid password", HttpStatus.UNAUTHORIZED);

        when(authService.login(any(LoginDTO.class))).thenThrow(exception);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid password"));
    }
}
