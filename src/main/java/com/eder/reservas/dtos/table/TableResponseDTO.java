package com.eder.reservas.dtos.table;

import com.eder.reservas.domain.table.TableStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TableResponseDTO(@NotNull UUID id, @NotNull int number, @NotNull int capacity, @NotNull TableStatus status) {
}
