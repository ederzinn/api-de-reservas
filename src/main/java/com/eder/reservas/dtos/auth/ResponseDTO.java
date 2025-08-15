package com.eder.reservas.dtos.auth;

import jakarta.validation.constraints.NotNull;

public record ResponseDTO(@NotNull(message = "token can not be null") String token, @NotNull(message = "email can not be null") String email) {
}
