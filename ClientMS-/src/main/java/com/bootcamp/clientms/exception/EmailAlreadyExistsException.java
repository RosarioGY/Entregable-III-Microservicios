package com.bootcamp.clientms.exception;

/**
 * Exception thrown when trying to register a client with an email that already exists.
 */
public class EmailAlreadyExistsException extends RuntimeException {

  public EmailAlreadyExistsException(String email) {
    super("Email already exists: " + email);
  }

  public EmailAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
