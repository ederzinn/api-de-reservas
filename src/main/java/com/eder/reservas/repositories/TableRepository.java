package com.eder.reservas.repositories;

import com.eder.reservas.domain.table.Table;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TableRepository extends JpaRepository<Table, UUID> {
    Optional<Table> findByNumber(int number);
}
