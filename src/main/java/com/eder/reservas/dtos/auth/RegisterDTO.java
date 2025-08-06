package com.eder.reservas.dtos.auth;

import com.eder.reservas.domain.user.UserRole;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(@NotNull String name, @NotNull String email, @NotNull String password, @NotNull UserRole role) {
}
