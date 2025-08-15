package com.eder.reservas.dtos.reservation;

import com.eder.reservas.domain.reservation.Reservation;
import com.eder.reservas.domain.reservation.ReservationStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponseDTO(
        @NotNull(message = "Reservation id can not be null") UUID id,
        @NotNull(message = "User id can not be null") UUID userId,
        @NotNull(message = "Table id can not be null") UUID tableId,
        @NotNull(message = "Reservation date time can not be null") LocalDateTime dateTime,
        @NotNull(message = "Amount of people can not be null") int people,
        @NotNull(message = "Reservation status can not be null") ReservationStatus status
        ) {
    public static ReservationResponseDTO from(Reservation reservation) {
        return new ReservationResponseDTO(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getTable().getId(),
                reservation.getReservationDateTime(),
                reservation.getNumberOfPeople(),
                reservation.getStatus()
        );
    }
}
