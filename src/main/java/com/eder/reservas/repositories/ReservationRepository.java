package com.eder.reservas.repositories;

import com.eder.reservas.domain.reservation.Reservation;
import com.eder.reservas.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findAllByUser(User user);
}
