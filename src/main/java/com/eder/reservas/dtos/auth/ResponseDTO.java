package com.eder.reservas.dtos.auth;

import jakarta.validation.constraints.NotNull;

public record ResponseDTO(@NotNull String token, @NotNull String email) {
}
