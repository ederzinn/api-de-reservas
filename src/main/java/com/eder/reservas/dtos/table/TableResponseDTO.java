package com.eder.reservas.dtos.table;

import com.eder.reservas.domain.table.TableStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TableResponseDTO(@NotNull(message = "id can not be null") UUID id, @NotNull(message = "Table number can not be null") int number, @NotNull(message = "Table capacity can not be null") int capacity, @NotNull(message = "Table status can not be null") TableStatus status) {
}
