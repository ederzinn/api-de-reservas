package com.eder.reservas.handler;

import com.eder.reservas.dtos.exception.ApiExceptionDTO;
import com.eder.reservas.dtos.exception.InvalidFieldDTO;
import com.eder.reservas.dtos.exception.ValidationExceptionDTO;
import com.eder.reservas.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiExceptionDTO> handleApiException(ApiException exception, HttpServletRequest request) {
        ApiExceptionDTO body = new ApiExceptionDTO(
                Instant.now(),
                exception.getErrorStatus().value(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(exception.getErrorStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<InvalidFieldDTO> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(e -> new InvalidFieldDTO(e.getField(), e.getDefaultMessage()))
                .collect(Collectors.toList());

        ValidationExceptionDTO body = new ValidationExceptionDTO(
                Instant.now(),
                status.value(),
                exception.getMessage(),
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(status).body(body);
    }
}
