package com.eder.reservas.services;

import com.eder.reservas.domain.user.User;
import com.eder.reservas.dtos.auth.LoginDTO;
import com.eder.reservas.dtos.auth.RegisterDTO;
import com.eder.reservas.dtos.auth.ResponseDTO;
import com.eder.reservas.exceptions.ApiException;
import com.eder.reservas.infra.security.TokenService;
import com.eder.reservas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
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
        throw new ApiException("Email already used", HttpStatus.CONFLICT);
    }

    @Transactional
    public ResponseDTO login(LoginDTO data) {
        User user = userRepository.findByEmail(data.email())
                .orElseThrow(() -> new ApiException("No user found for this email", HttpStatus.NOT_FOUND));

        if(passwordEncoder.matches(data.password(), user.getPassword())) {
            String token = tokenService.generateToken(user);

            return new ResponseDTO(token, user.getEmail());
        }
        throw new ApiException("Invalid password", HttpStatus.UNAUTHORIZED);
    }
}
