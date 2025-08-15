package com.eder.reservas.dtos.auth;

import jakarta.validation.constraints.NotNull;

public record LoginDTO(@NotNull(message = "email can not be null") String email, @NotNull(message = "password can not be null") String password) {
}
