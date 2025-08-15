package com.eder.reservas.dtos.exception;

import jakarta.validation.constraints.NotNull;

public record InvalidFieldDTO(@NotNull String field, @NotNull String message) {
}
