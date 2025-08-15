package com.eder.reservas.controllers;

import com.eder.reservas.dtos.auth.LoginDTO;
import com.eder.reservas.dtos.auth.RegisterDTO;
import com.eder.reservas.dtos.auth.ResponseDTO;
import com.eder.reservas.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterDTO data) {
        ResponseDTO response = authService.register(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@Valid @RequestBody LoginDTO data) {
        ResponseDTO response = authService.login(data);
        return ResponseEntity.ok(response);
    }
}
