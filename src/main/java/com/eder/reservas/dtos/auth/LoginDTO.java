package com.eder.reservas.dtos.auth;

import jakarta.validation.constraints.NotNull;

public record LoginDTO(@NotNull String email, @NotNull String password) {
}
