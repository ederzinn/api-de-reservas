package com.eder.reservas.controllers;

import com.eder.reservas.domain.reservation.ReservationStatus;
import com.eder.reservas.domain.user.User;
import com.eder.reservas.domain.user.UserRole;
import com.eder.reservas.dtos.reservation.ReservationRegisterDTO;
import com.eder.reservas.dtos.reservation.ReservationResponseDTO;
import com.eder.reservas.exceptions.ApiException;
import com.eder.reservas.infra.security.SecurityConfig;
import com.eder.reservas.infra.security.TokenService;
import com.eder.reservas.repositories.UserRepository;
import com.eder.reservas.services.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@WebMvcTest(ReservationController.class)
@WithMockUser(roles = "ADMIN")
@Import(SecurityConfig.class)
public class ReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ReservationService reservationService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TokenService tokenService;

    @Test
    public void shouldCreateReservationAndReturn201() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timestamp = now.truncatedTo(ChronoUnit.SECONDS);

        ReservationRegisterDTO data = new ReservationRegisterDTO(
                1,
                timestamp,
                2
        );

        User user = new User();
        user.setName("name");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);

        ReservationResponseDTO response = new ReservationResponseDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                timestamp,
                2,
                ReservationStatus.ACTIVE
        );

        when(reservationService.createReservation(any(ReservationRegisterDTO.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/reservations")
                .with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dateTime").value(timestamp.toString()))
                .andExpect(jsonPath("$.people").value(2))
                .andExpect(jsonPath("$.status").value(ReservationStatus.ACTIVE.name()));
    }

    @Test
    public void shouldNotCreateReservationAndThrow404() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timestamp = now.truncatedTo(ChronoUnit.SECONDS);

        ReservationRegisterDTO data = new ReservationRegisterDTO(
                1,
                timestamp,
                2
        );

        User user = new User();
        user.setName("name");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);

        ApiException exception = new ApiException("Table does not exist", HttpStatus.NOT_FOUND);

        when(reservationService.createReservation(any(ReservationRegisterDTO.class), anyString())).thenThrow(exception);

        mockMvc.perform(post("/reservations")
                .with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Table does not exist"));
    }

    @Test
    public void shouldNotCreateReservationAndThrow409() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timestamp = now.truncatedTo(ChronoUnit.SECONDS);

        ReservationRegisterDTO data = new ReservationRegisterDTO(
                1,
                timestamp,
                2
        );

        User user = new User();
        user.setName("name");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);

        ApiException exception = new ApiException("Table is unavailable", HttpStatus.CONFLICT);

        when(reservationService.createReservation(any(ReservationRegisterDTO.class), anyString())).thenThrow(exception);

        mockMvc.perform(post("/reservations")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Table is unavailable"));
    }

    @Test
    public void shouldNotCreateReservationAndThrow400() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timestamp = now.truncatedTo(ChronoUnit.SECONDS);

        ReservationRegisterDTO data = new ReservationRegisterDTO(
                1,
                timestamp,
                2
        );

        User user = new User();
        user.setName("name");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);

        ApiException exception = new ApiException("Amount of people exceeded", HttpStatus.BAD_REQUEST);

        when(reservationService.createReservation(any(ReservationRegisterDTO.class), anyString())).thenThrow(exception);

        mockMvc.perform(post("/reservations")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount of people exceeded"));
    }

    @Test
    public void shouldGetAllUserReservations() throws Exception {
        User user = new User();
        user.setName("name");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timestamp = now.truncatedTo(ChronoUnit.SECONDS);

        ReservationResponseDTO reservations = new ReservationResponseDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                timestamp,
                2,
                ReservationStatus.ACTIVE
        );

        List<ReservationResponseDTO> response = List.of(reservations);

        when(reservationService.getAllReservations(anyString())).thenReturn(response);

        mockMvc.perform(get("/reservations")
                .with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].dateTime").value(timestamp.toString()))
                .andExpect(jsonPath("$.[0].people").value(2))
                .andExpect(jsonPath("$.[0].status").value(ReservationStatus.ACTIVE.name()));
    }

    @Test
    public void shouldNotGetAllUserReservationsAndThrow404() throws Exception {
        User user = new User();
        user.setName("name");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);

        ApiException exception = new ApiException("User not found", HttpStatus.NOT_FOUND);

        when(reservationService.getAllReservations(anyString())).thenThrow(exception);

        mockMvc.perform(get("/reservations")
                .with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(user.getEmail()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    public void shouldCancelReservationAndReturn200() throws Exception {
        User user = new User();
        user.setName("name");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);

        UUID id = UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timestamp = now.truncatedTo(ChronoUnit.SECONDS);

        ReservationResponseDTO reservations = new ReservationResponseDTO(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                timestamp,
                2,
                ReservationStatus.CANCELED
        );

        when(reservationService.patchReservation(any(UUID.class))).thenReturn(reservations);

        mockMvc.perform(patch("/reservations/{id}/cancel", id)
                .with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateTime").value(timestamp.toString()))
                .andExpect(jsonPath("$.people").value(2))
                .andExpect(jsonPath("$.status").value(ReservationStatus.CANCELED.name()));
    }

    @Test
    public void shouldNotCancelReservationAndThrow404() throws Exception {
        User user = new User();
        user.setName("name");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);

        UUID id = UUID.randomUUID();

        ApiException exception = new ApiException("Reservation not found", HttpStatus.NOT_FOUND);

        when(reservationService.patchReservation(any(UUID.class))).thenThrow(exception);

        mockMvc.perform(patch("/reservations/{id}/cancel", id)
                .with(user(user)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Reservation not found"));
    }
}
