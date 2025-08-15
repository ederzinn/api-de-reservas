package com.eder.reservas.dtos.exception;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record ValidationExceptionDTO(
        @NotNull Instant timestamp,
        @NotNull int status,
        @NotNull String message,
        @NotNull String path,
        List<InvalidFieldDTO> invalidField
) {
}
