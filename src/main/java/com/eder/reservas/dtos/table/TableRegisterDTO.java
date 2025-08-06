package com.eder.reservas.dtos.table;

import com.eder.reservas.domain.table.TableStatus;
import jakarta.validation.constraints.NotNull;

public record TableRegisterDTO(@NotNull int number, @NotNull int capacity, @NotNull TableStatus status) {
}
