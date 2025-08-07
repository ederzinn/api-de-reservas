package com.eder.reservas.services;

import com.eder.reservas.domain.reservation.Reservation;
import com.eder.reservas.domain.reservation.ReservationStatus;
import com.eder.reservas.domain.table.Table;
import com.eder.reservas.domain.table.TableStatus;
import com.eder.reservas.domain.user.User;
import com.eder.reservas.dtos.reservation.ReservationRegisterDTO;
import com.eder.reservas.dtos.reservation.ReservationResponseDTO;
import com.eder.reservas.repositories.ReservationRepository;
import com.eder.reservas.repositories.TableRepository;
import com.eder.reservas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;

    public ReservationResponseDTO createReservation(ReservationRegisterDTO data) {
        Table table = tableRepository.findByNumber(data.tableNumber())
                .orElseThrow(() -> new RuntimeException("Table does not exist"));

        if(table.getStatus() == TableStatus.UNAVAILABLE) {
            throw new RuntimeException("Table already reserved");
        }
        if(data.people() > table.getCapacity()) {
            throw new RuntimeException("Amount of people exceeded");
        }
        User user = userRepository.findByEmail(this.getAuthenticatedEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reservation newReservation = new Reservation();
        newReservation.setUser(user);
        newReservation.setTable(table);
        newReservation.setReservation_date_time(data.dateTime());
        newReservation.setNumber_of_people(data.people());
        newReservation.setStatus(ReservationStatus.ACTIVE);

        reservationRepository.save(newReservation);

        return ReservationResponseDTO.from(newReservation);
    }

    public List<ReservationResponseDTO> getAllReservations() {
        User user = userRepository.findByEmail(this.getAuthenticatedEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return reservationRepository.findAllByUser(user).stream()
                .map(r -> ReservationResponseDTO.from(r))
                .collect(Collectors.toList());
    }

    public ReservationResponseDTO patchReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatus(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);

        return ReservationResponseDTO.from(reservation);
    }

    private String getAuthenticatedEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
