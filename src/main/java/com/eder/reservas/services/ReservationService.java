package com.eder.reservas.services;

import com.eder.reservas.domain.reservation.Reservation;
import com.eder.reservas.domain.reservation.ReservationStatus;
import com.eder.reservas.domain.table.Table;
import com.eder.reservas.domain.table.TableStatus;
import com.eder.reservas.domain.user.User;
import com.eder.reservas.dtos.reservation.ReservationRegisterDTO;
import com.eder.reservas.dtos.reservation.ReservationResponseDTO;
import com.eder.reservas.exceptions.ApiException;
import com.eder.reservas.repositories.ReservationRepository;
import com.eder.reservas.repositories.TableRepository;
import com.eder.reservas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReservationResponseDTO createReservation(ReservationRegisterDTO data, String email) {
        Table table = tableRepository.findByNumber(data.tableNumber())
                .orElseThrow(() -> new ApiException("Table does not exist", HttpStatus.NOT_FOUND));

        if(table.getStatus() == TableStatus.UNAVAILABLE || table.getStatus() == TableStatus.INACTIVE) {
            throw new ApiException("Table is unavailable", HttpStatus.CONFLICT);
        }
        if(data.people() > table.getCapacity()) {
            throw new ApiException("Amount of people exceeded", HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        table.setStatus(TableStatus.UNAVAILABLE);

        Reservation newReservation = new Reservation();
        newReservation.setUser(user);
        newReservation.setTable(table);
        newReservation.setReservationDateTime(data.dateTime());
        newReservation.setNumberOfPeople(data.people());
        newReservation.setStatus(ReservationStatus.ACTIVE);

        tableRepository.save(table);
        reservationRepository.save(newReservation);

        return ReservationResponseDTO.from(newReservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getAllReservations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        return reservationRepository.findAllByUser(user).stream()
                .map(r -> ReservationResponseDTO.from(r))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponseDTO patchReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Reservation not found", HttpStatus.NOT_FOUND));

        Table table = reservation.getTable();
        table.setStatus(TableStatus.AVAILABLE);
        tableRepository.save(table);

        reservation.setStatus(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);

        return ReservationResponseDTO.from(reservation);
    }
}
