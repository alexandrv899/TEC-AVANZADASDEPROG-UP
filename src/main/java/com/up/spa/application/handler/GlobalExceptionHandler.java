package com.up.spa.application.handler;

import com.up.spa.application.exception.InvalidTurnoDataException;
import com.up.spa.application.exception.ResourceNotFoundException;
import com.up.spa.application.exception.TurnoNoDisponibleException;
import com.up.spa.application.model.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage()));
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations().forEach(violation ->
        errors.put(violation.getPropertyPath().toString(), violation.getMessage())
    );
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
    ErrorResponse error = ErrorResponse.builder()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .error("Recurso no encontrado")
        .message(ex.getMessage())
        .build();
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidTurnoDataException.class)
  public ResponseEntity<ErrorResponse> handleInvalidTurnoDataException(InvalidTurnoDataException ex) {
    ErrorResponse error = ErrorResponse.builder()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .error("Datos de turno inválidos")
        .message(ex.getMessage())
        .build();
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TurnoNoDisponibleException.class)
  public ResponseEntity<ErrorResponse> handleTurnoNoDisponibleException(TurnoNoDisponibleException ex) {
    ErrorResponse error = ErrorResponse.builder()
        .statusCode(HttpStatus.CONFLICT.value())
        .error("Turno No Disponible")
        .message(ex.getMessage())
        .sugerencias(ex.getSugerencias())  // Incluir las sugerencias aquí
        .build();
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
    ErrorResponse error = ErrorResponse.builder()
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Error Interno del Servidor")
        .message("Ocurrió un error inesperado. Por favor, contacte al administrador.")
        .build();
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
