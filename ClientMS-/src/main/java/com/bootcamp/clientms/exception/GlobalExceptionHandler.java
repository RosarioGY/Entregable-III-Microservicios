package com.bootcamp.clientms.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the application. Handles all exceptions and provides consistent
 * error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final DateTimeFormatter TIMESTAMP_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  /**
   * Handles client not found exceptions.
   */
  @ExceptionHandler(ClientNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleClientNotFoundException(
      ClientNotFoundException ex, WebRequest request) {
    log.warn("Client not found: {}", ex.getMessage());

    Map<String, Object> errorDetails = createErrorResponse(HttpStatus.NOT_FOUND, "Not Found",
        ex.getMessage(), request.getDescription(false).replace("uri=", ""));

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
  }

  /**
   * Handles email already exists exceptions.
   */
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<Map<String, Object>> handleEmailAlreadyExistsException(
      EmailAlreadyExistsException ex, WebRequest request) {
    log.warn("Email conflict: {}", ex.getMessage());

    Map<String, Object> errorDetails = createErrorResponse(HttpStatus.CONFLICT, "Conflict",
        ex.getMessage(), request.getDescription(false).replace("uri=", ""));

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
  }

  /**
   * Handles DNI already exists exceptions.
   */
  @ExceptionHandler(DniAlreadyExistsException.class)
  public ResponseEntity<Map<String, Object>> handleDniAlreadyExistsException(
      DniAlreadyExistsException ex, WebRequest request) {
    log.warn("DNI conflict: {}", ex.getMessage());

    Map<String, Object> errorDetails = createErrorResponse(HttpStatus.CONFLICT, "Conflict",
        ex.getMessage(), request.getDescription(false).replace("uri=", ""));

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
  }

  /**
   * Handles validation errors from @Valid annotations.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {
    log.warn("Validation error: {}", ex.getMessage());

    StringBuilder errorMessage = new StringBuilder("Validation failed: ");
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String message = error.getDefaultMessage();
      errorMessage.append(fieldName).append(" - ").append(message).append("; ");
    });

    Map<String, Object> errorDetails = createErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request",
        errorMessage.toString().trim(), request.getDescription(false).replace("uri=", ""));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
  }

  /**
   * Handles all other unexpected exceptions.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex,
      WebRequest request) {
    log.error("Unexpected error occurred: ", ex);

    Map<String, Object> errorDetails = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        "Internal Server Error", "An unexpected error occurred. Please try again later.",
        request.getDescription(false).replace("uri=", ""));

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
  }

  /**
   * Creates a standardized error response.
   */
  private Map<String, Object> createErrorResponse(HttpStatus status, String error, String message,
      String path) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMAT));
    errorDetails.put("status", status.value());
    errorDetails.put("error", error);
    errorDetails.put("message", message);
    errorDetails.put("path", path);
    return errorDetails;
  }
}
