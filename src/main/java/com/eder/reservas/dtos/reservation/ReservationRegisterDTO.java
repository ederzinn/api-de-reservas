package com.eder.reservas.dtos.reservation;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationRegisterDTO(
        @NotNull(message = "Table number can not be null") int tableNumber,
        @NotNull(message = "Reservation date time can not be null") LocalDateTime dateTime,
        @NotNull(message = "Amount of people can not be null") int people
) {
}
