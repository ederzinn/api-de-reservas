package com.eder.reservas.dtos.auth;

import com.eder.reservas.domain.user.UserRole;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(@NotNull(message = "name can not be null") String name, @NotNull(message = "email can not be null") String email, @NotNull(message = "password can not be null") String password, @NotNull(message = "role can not be null") UserRole role) {
}
