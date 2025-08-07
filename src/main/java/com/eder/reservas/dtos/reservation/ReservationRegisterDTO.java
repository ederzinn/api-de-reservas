package com.eder.reservas.dtos.reservation;

import java.time.LocalDateTime;

public record ReservationRegisterDTO(
        int tableNumber,
        LocalDateTime dateTime,
        int people
) {
}
