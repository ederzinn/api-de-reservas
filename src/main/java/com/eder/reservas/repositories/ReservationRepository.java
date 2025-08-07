package com.eder.reservas.repositories;

import com.eder.reservas.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
}
