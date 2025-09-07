package com.bootcamp.clientms.exception;

/**
 * Exception thrown when trying to register a client with a DNI that already exists.
 */
public class DniAlreadyExistsException extends RuntimeException {

  public DniAlreadyExistsException(String dni) {
    super("DNI already exists: " + dni);
  }

  public DniAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
