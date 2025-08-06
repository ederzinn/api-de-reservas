package com.eder.reservas.controllers;

import com.eder.reservas.dtos.LoginDTO;
import com.eder.reservas.dtos.RegisterDTO;
import com.eder.reservas.dtos.ResponseDTO;
import com.eder.reservas.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterDTO data) {
        ResponseDTO response = authService.register(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@Valid @RequestBody LoginDTO data) {
        ResponseDTO response = authService.login(data);
        return ResponseEntity.ok(response);
    }
}
