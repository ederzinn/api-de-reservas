package com.eder.reservas.controllers;

import com.eder.reservas.domain.user.User;
import com.eder.reservas.dtos.reservation.ReservationRegisterDTO;
import com.eder.reservas.dtos.reservation.ReservationResponseDTO;
import com.eder.reservas.services.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @Transactional
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(@Valid @RequestBody ReservationRegisterDTO data, @AuthenticationPrincipal User user) {
        String email = user.getEmail();

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(data, email));
    }

    @Transactional
    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations(@AuthenticationPrincipal User user) {
        String email = user.getEmail();

        return ResponseEntity.ok(reservationService.getAllReservations(email));
    }

    @Transactional
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponseDTO> patchReservation(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.patchReservation(id));
    }
}
