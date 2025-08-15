package com.eder.reservas.dtos.exception;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ApiExceptionDTO(
        @NotNull Instant timestamp,
        @NotNull int status,
        @NotNull String message,
        @NotNull String path
        ) {
}
