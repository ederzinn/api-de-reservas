package com.eder.reservas.dtos.reservation;

import com.eder.reservas.domain.reservation.Reservation;
import com.eder.reservas.domain.reservation.ReservationStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponseDTO(
        @NotNull UUID id,
        @NotNull UUID userId,
        @NotNull UUID tableId,
        @NotNull LocalDateTime dateTime,
        @NotNull int people,
        @NotNull ReservationStatus status
        ) {
    public static ReservationResponseDTO from(Reservation reservation) {
        return new ReservationResponseDTO(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getTable().getId(),
                reservation.getReservation_date_time(),
                reservation.getNumber_of_people(),
                reservation.getStatus()
        );
    }
}
