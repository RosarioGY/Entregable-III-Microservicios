package com.bootcamp.clientms.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bootcamp.clientms.domain.Client;

/**
 * Repository interface for Client entity operations. Provides CRUD operations and custom queries
 * for client management.
 */
@Repository
public interface ClientRepository extends MongoRepository<Client, String> {

  /**
   * Finds a client by email address.
   *
   * @param email the email address to search for
   * @return Optional containing the client if found, empty otherwise
   */
  Optional<Client> findByEmail(String email);

  /**
   * Finds a client by DNI (Document National Identity).
   *
   * @param dni the DNI to search for
   * @return Optional containing the client if found, empty otherwise
   */
  Optional<Client> findByDni(String dni);

  /**
   * Checks if a client exists with the given email address.
   *
   * @param email the email address to check
   * @return true if a client exists with the email, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Checks if a client exists with the given DNI.
   *
   * @param dni the DNI to check
   * @return true if a client exists with the DNI, false otherwise
   */
  boolean existsByDni(String dni);

  /**
   * Checks if a client exists with the given email, excluding a specific client ID. Useful for
   * update operations to avoid conflicts with the same client.
   *
   * @param email the email address to check
   * @param id the client ID to exclude from the search
   * @return true if another client exists with the email, false otherwise
   */
  boolean existsByEmailAndIdNot(String email, String id);

  /**
   * Checks if a client exists with the given DNI, excluding a specific client ID. Useful for update
   * operations to avoid conflicts with the same client.
   *
   * @param dni the DNI to check
   * @param id the client ID to exclude from the search
   * @return true if another client exists with the DNI, false otherwise
   */
  boolean existsByDniAndIdNot(String dni, String id);
}
