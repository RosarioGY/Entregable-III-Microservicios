package com.bootcamp.transactions.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.bootcamp.transactions.dto_2.request.CreateClientRequest;
import com.bootcamp.transactions.dto_2.request.PatchClientRequest;
import com.bootcamp.transactions.dto_2.request.UpdateClientRequest;
import com.bootcamp.transactions.dto_2.response.ClientResponse;
import com.bootcamp.transactions.exception.ClientNotFoundException;
import com.bootcamp.transactions.exception.DniAlreadyExistsException;
import com.bootcamp.transactions.exception.EmailAlreadyExistsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing client operations. Handles business logic, validations, and data
 * transformations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

  private final ClientRepository clientRepository;

  /**
   * Creates a new client after validating email and DNI uniqueness.
   *
   * @param request the client creation request
   * @return the created client response
   * @throws EmailAlreadyExistsException if email already exists
   * @throws DniAlreadyExistsException if DNI already exists
   */
  public ClientResponse createClient(CreateClientRequest request) {
    log.info("Creating new client with email: {}", request.getEmail());

    validateEmailUniqueness(request.getEmail());
    validateDniUniqueness(request.getDni());

    Client client = Client.builder().firstName(request.getFirstName())
        .lastName(request.getLastName()).email(request.getEmail()).dni(request.getDni()).build();

    Client savedClient = clientRepository.save(client);
    log.info("Client created successfully with id: {}", savedClient.getId());

    return ClientResponse.from(savedClient);
  }

  /**
   * Retrieves all clients from the database.
   *
   * @return list of all client responses
   */
  public List<ClientResponse> getAllClients() {
    log.info("Retrieving all clients");
    return clientRepository.findAll().stream().map(ClientResponse::from).toList();
  }

  /**
   * Retrieves a client by ID.
   *
   * @param id the client ID
   * @return the client response
   * @throws ClientNotFoundException if client is not found
   */
  public ClientResponse getClientById(String id) {
    log.info("Retrieving client with id: {}", id);
    return ClientResponse.from(findClientByIdOrThrow(id));
  }

  /**
   * Updates a client completely (PUT operation).
   *
   * @param id the client ID to update
   * @param request the update request
   * @return the updated client response
   * @throws ClientNotFoundException if client is not found
   * @throws EmailAlreadyExistsException if email already exists for another client
   */
  public ClientResponse updateClient(String id, UpdateClientRequest request) {
    log.info("Updating client with id: {}", id);

    Client existingClient = findClientByIdOrThrow(id);
    validateEmailUniquenessForUpdate(request.getEmail(), id);

    existingClient.setFirstName(request.getFirstName());
    existingClient.setLastName(request.getLastName());
    existingClient.setEmail(request.getEmail());

    Client updatedClient = clientRepository.save(existingClient);
    log.info("Client updated successfully with id: {}", updatedClient.getId());

    return ClientResponse.from(updatedClient);
  }

  /**
   * Partially updates a client (PATCH operation).
   *
   * @param id the client ID to update
   * @param request the patch request
   * @return the updated client response
   * @throws ClientNotFoundException if client is not found
   * @throws EmailAlreadyExistsException if email already exists for another client
   * @throws DniAlreadyExistsException if DNI already exists for another client
   */
  public ClientResponse patchClient(String id, PatchClientRequest request) {
    log.info("Patching client with id: {}", id);

    Client existingClient = findClientByIdOrThrow(id);

    if (request.getFirstName() != null) {
      existingClient.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      existingClient.setLastName(request.getLastName());
    }
    if (request.getEmail() != null) {
      validateEmailUniquenessForUpdate(request.getEmail(), id);
      existingClient.setEmail(request.getEmail());
    }
    if (request.getDni() != null) {
      validateDniUniquenessForUpdate(request.getDni(), id);
      existingClient.setDni(request.getDni());
    }

    Client updatedClient = clientRepository.save(existingClient);
    log.info("Client patched successfully with id: {}", updatedClient.getId());

    return ClientResponse.from(updatedClient);
  }

  /**
   * Deletes a client by ID.
   *
   * @param id the client ID to delete
   * @throws ClientNotFoundException if client is not found
   */
  public void deleteClient(String id) {
    log.info("Deleting client with id: {}", id);
    Client client = findClientByIdOrThrow(id);
    clientRepository.delete(client);
    log.info("Client deleted successfully with id: {}", id);
  }

  /**
   * Helper method to find a client by ID or throw exception if not found.
   */
  private Client findClientByIdOrThrow(String id) {
    return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
  }

  /**
   * Validates that the email is unique in the database.
   */
  private void validateEmailUniqueness(String email) {
    if (clientRepository.existsByEmail(email)) {
      log.warn("Attempt to create client with existing email: {}", email);
      throw new EmailAlreadyExistsException(email);
    }
  }

  /**
   * Validates that the DNI is unique in the database.
   */
  private void validateDniUniqueness(String dni) {
    if (clientRepository.existsByDni(dni)) {
      log.warn("Attempt to create client with existing DNI: {}", dni);
      throw new DniAlreadyExistsException(dni);
    }
  }

  /**
   * Validates email uniqueness for update operations, excluding the current client.
   */
  private void validateEmailUniquenessForUpdate(String email, String currentClientId) {
    if (clientRepository.existsByEmailAndIdNot(email, currentClientId)) {
      log.warn("Attempt to update client with existing email: {}", email);
      throw new EmailAlreadyExistsException(email);
    }
  }

  /**
   * Validates DNI uniqueness for update operations, excluding the current client.
   */
  private void validateDniUniquenessForUpdate(String dni, String currentClientId) {
    if (clientRepository.existsByDniAndIdNot(dni, currentClientId)) {
      log.warn("Attempt to update client with existing DNI: {}", dni);
      throw new DniAlreadyExistsException(dni);
    }
  }
}
