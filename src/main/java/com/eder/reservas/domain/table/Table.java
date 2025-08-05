package com.eder.reservas.domain.table;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "restaurant_tables")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int number;
    private int capacity;

    @Enumerated(EnumType.STRING)
    private TableStatus status;
}
