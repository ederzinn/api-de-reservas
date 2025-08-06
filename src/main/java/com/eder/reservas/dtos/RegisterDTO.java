package com.eder.reservas.dtos;

import com.eder.reservas.domain.user.UserRole;

public record RegisterDTO(String name, String email, String password, UserRole role) {
}
