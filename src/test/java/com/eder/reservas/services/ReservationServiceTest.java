package com.eder.reservas.services;

import com.eder.reservas.domain.reservation.Reservation;
import com.eder.reservas.domain.reservation.ReservationStatus;
import com.eder.reservas.domain.table.Table;
import com.eder.reservas.domain.table.TableStatus;
import com.eder.reservas.domain.user.User;
import com.eder.reservas.domain.user.UserRole;
import com.eder.reservas.dtos.reservation.ReservationRegisterDTO;
import com.eder.reservas.dtos.reservation.ReservationResponseDTO;
import com.eder.reservas.exceptions.ApiException;
import com.eder.reservas.repositories.ReservationRepository;
import com.eder.reservas.repositories.TableRepository;
import com.eder.reservas.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private TableRepository tableRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    public void shouldCreateReservation() {
        ReservationRegisterDTO newReservation = new ReservationRegisterDTO(
                1,
                LocalDateTime.now(),
                2
        );

        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(2);
        table.setStatus(TableStatus.AVAILABLE);

        User user = new User();
        user.setName("name");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole(UserRole.USER);

        Reservation createdReservation = new Reservation();
        createdReservation.setUser(user);
        createdReservation.setTable(table);
        createdReservation.setReservation_date_time(newReservation.dateTime());
        createdReservation.setNumber_of_people(2);
        createdReservation.setStatus(ReservationStatus.ACTIVE);

        when(tableRepository.findByNumber(anyInt())).thenReturn(Optional.of(table));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(tableRepository.save(any(Table.class))).thenReturn(table);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(createdReservation);

        ReservationResponseDTO response = reservationService.createReservation(newReservation, user.getEmail());

        assertNotNull(response);
        assertEquals(TableStatus.UNAVAILABLE, table.getStatus());
        assertEquals(user.getId(), response.userId());
        assertEquals(table.getId(), response.tableId());
        assertEquals(newReservation.dateTime(), response.dateTime());
        assertEquals(2, response.people());
        assertEquals(ReservationStatus.ACTIVE, response.status());
        verify(tableRepository, times(1)).findByNumber(anyInt());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(tableRepository, times(1)).save(any(Table.class));
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    public void shouldNotCreateReservationIfTableDoesNotExists() {
        ReservationRegisterDTO newReservation = new ReservationRegisterDTO(
                1,
                LocalDateTime.now(),
                2
        );

        String email = "test@email.com";

        when(tableRepository.findByNumber(anyInt())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            reservationService.createReservation(newReservation, email);
        });
        assertEquals("Table does not exist", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getErrorStatus());
        verify(tableRepository, times(1)).findByNumber(anyInt());
        verify(userRepository, never()).findByEmail(anyString());
        verify(tableRepository, never()).save(any(Table.class));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    public void shouldNotCreateReservationIfTableIsNotAvailable() {
        ReservationRegisterDTO newReservation = new ReservationRegisterDTO(
                1,
                LocalDateTime.now(),
                2
        );

        String email = "test@email.com";

        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(2);
        table.setStatus(TableStatus.UNAVAILABLE);

        when(tableRepository.findByNumber(anyInt())).thenReturn(Optional.of(table));

        ApiException exception = assertThrows(ApiException.class, () -> {
            reservationService.createReservation(newReservation, email);
        });
        assertEquals("Table is unavailable", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getErrorStatus());
        verify(tableRepository, times(1)).findByNumber(anyInt());
        verify(userRepository, never()).findByEmail(anyString());
        verify(tableRepository, never()).save(any(Table.class));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    public void shouldNotCreateReservationIfTableCapacityIsExceeded() {
        ReservationRegisterDTO newReservation = new ReservationRegisterDTO(
                1,
                LocalDateTime.now(),
                3
        );

        String email = "test@email.com";

        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(2);
        table.setStatus(TableStatus.AVAILABLE);

        when(tableRepository.findByNumber(anyInt())).thenReturn(Optional.of(table));

        ApiException exception = assertThrows(ApiException.class, () -> {
            reservationService.createReservation(newReservation, email);
        });
        assertEquals("Amount of people exceeded", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getErrorStatus());
        verify(tableRepository, times(1)).findByNumber(anyInt());
        verify(userRepository, never()).findByEmail(anyString());
        verify(tableRepository, never()).save(any(Table.class));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    public void shouldNotCreateReservationIfUserDoNotExists() {
        ReservationRegisterDTO newReservation = new ReservationRegisterDTO(
                1,
                LocalDateTime.now(),
                2
        );

        String email = "test@email.com";

        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(2);
        table.setStatus(TableStatus.AVAILABLE);

        when(tableRepository.findByNumber(anyInt())).thenReturn(Optional.of(table));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            reservationService.createReservation(newReservation, email);
        });
        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getErrorStatus());
        verify(tableRepository, times(1)).findByNumber(anyInt());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(tableRepository, never()).save(any(Table.class));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    public void shouldGetAllUsersReservations() {
        String email = "test@email.com";

        User user = new User();
        user.setName("name");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole(UserRole.USER);

        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(2);
        table.setStatus(TableStatus.AVAILABLE);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setReservation_date_time(LocalDateTime.now());
        reservation.setNumber_of_people(2);
        reservation.setStatus(ReservationStatus.ACTIVE);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(reservationRepository.findAllByUser(any(User.class))).thenReturn(List.of(reservation));

        List<ReservationResponseDTO> response = reservationService.getAllReservations(email);
        assertNotNull(response);
        assertEquals(user.getId(), response.get(0).userId());
        assertEquals(table.getId(), response.get(0).tableId());
        assertEquals(reservation.getReservation_date_time(), response.get(0).dateTime());
        assertEquals(2, response.get(0).people());
        assertEquals(ReservationStatus.ACTIVE, response.get(0).status());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(reservationRepository, times(1)).findAllByUser(any(User.class));
    }

    @Test
    public void shouldNotGetReservationsIfUserDoesNotExists() {
        String email = "test@email.com";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            reservationService.getAllReservations(email);
        });
        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(reservationRepository, never()).findAllByUser(any(User.class));
    }

    @Test
    public void shouldCancelReservation() {
        UUID id = UUID.randomUUID();

        User user = new User();
        user.setName("name");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole(UserRole.USER);

        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(2);
        table.setStatus(TableStatus.UNAVAILABLE);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setReservation_date_time(LocalDateTime.now());
        reservation.setNumber_of_people(2);
        reservation.setStatus(ReservationStatus.ACTIVE);

        when(reservationRepository.findById(any(UUID.class))).thenReturn(Optional.of(reservation));
        when(tableRepository.save(any(Table.class))).thenReturn(table);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationResponseDTO response = reservationService.patchReservation(id);

        assertNotNull(response);
        assertEquals(TableStatus.AVAILABLE, table.getStatus());
        assertEquals(user.getId(), response.userId());
        assertEquals(table.getId(), response.tableId());
        assertEquals(reservation.getReservation_date_time(), response.dateTime());
        assertEquals(2, response.people());
        assertEquals(ReservationStatus.CANCELED, response.status());
        verify(reservationRepository, times(1)).findById(any(UUID.class));
        verify(tableRepository, times(1)).save(any(Table.class));
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    public void shouldNotCancelReservationIfDoesNotExists() {
        UUID id = UUID.randomUUID();

        when(reservationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            reservationService.patchReservation(id);
        });
        assertEquals("Reservation not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getErrorStatus());
        verify(reservationRepository, times(1)).findById(any(UUID.class));
        verify(tableRepository, never()).save(any(Table.class));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}
