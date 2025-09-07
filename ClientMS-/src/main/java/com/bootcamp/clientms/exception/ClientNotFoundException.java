package com.bootcamp.clientms.exception;

/**
 * Exception thrown when a client is not found in the database.
 */
public class ClientNotFoundException extends RuntimeException {

  public ClientNotFoundException(String id) {
    super("Client not found with id: " + id);
  }
}
