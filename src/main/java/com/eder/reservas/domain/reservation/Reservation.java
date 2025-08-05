package com.eder.reservas.domain.reservation;

import com.eder.reservas.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import com.eder.reservas.domain.table.Table;

@Entity
@jakarta.persistence.Table(name = "reservations")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private Table table;

    private LocalDateTime reservation_date_time;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
}
