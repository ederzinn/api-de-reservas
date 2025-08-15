package com.eder.reservas.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiException extends RuntimeException {
  private HttpStatus errorStatus;

  public ApiException(String message, HttpStatus status) {
      super(message);
      this.errorStatus = status;
  }
}
