package com.eder.reservas.dtos.table;

import com.eder.reservas.domain.table.TableStatus;

import java.util.Optional;

public record TablePatchDTO(Optional<Integer> number, Optional<Integer> capacity, Optional<TableStatus> status) {
}
