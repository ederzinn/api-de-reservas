package com.eder.reservas.dtos.table;

import com.eder.reservas.domain.table.TableStatus;
import jakarta.validation.constraints.NotNull;

public record TableRegisterDTO(@NotNull(message = "Table number can not be null") int number, @NotNull(message = "Table capacity can not be null") int capacity, @NotNull(message = "Table status can not be null") TableStatus status) {
}
