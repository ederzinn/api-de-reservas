package com.eder.reservas.services;

import com.eder.reservas.domain.user.User;
import com.eder.reservas.dtos.auth.LoginDTO;
import com.eder.reservas.dtos.auth.RegisterDTO;
import com.eder.reservas.dtos.auth.ResponseDTO;
import com.eder.reservas.infra.security.TokenService;
import com.eder.reservas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public ResponseDTO register(RegisterDTO data) {
        Optional<User> user = userRepository.findByEmail(data.email());

        if(user.isEmpty()) {
            User newUser = new User();
            newUser.setName(data.name());
            newUser.setEmail(data.email());
            newUser.setPassword(passwordEncoder.encode(data.password()));
            newUser.setRole(data.role());

            userRepository.save(newUser);
            String token = tokenService.generateToken(newUser);

            return new ResponseDTO(token, newUser.getEmail());
        }
        throw new RuntimeException("Email already used");
    }

    public ResponseDTO login(LoginDTO data) {
        User user = userRepository.findByEmail(data.email())
                .orElseThrow(() -> new RuntimeException("No user found for this email"));

        if(passwordEncoder.matches(data.password(), user.getPassword())) {
            String token = tokenService.generateToken(user);

            return new ResponseDTO(token, user.getEmail());
        }
        throw new RuntimeException("Invalid password");
    }
}
