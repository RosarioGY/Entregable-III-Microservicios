package com.bootcamp.transactions.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bootcamp.transactions.dto_2.request.CreateClientRequest;
import com.bootcamp.transactions.dto_2.request.PatchClientRequest;
import com.bootcamp.transactions.dto_2.request.UpdateClientRequest;
import com.bootcamp.transactions.dto_2.response.ClientResponse;
import com.bootcamp.transactions.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing client operations. Provides endpoints for CRUD operations following
 * OpenAPI specification.
 */
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client", description = "Customer-related operations")
public class ClientController {

  private final ClientService clientService;

  /**
   * Creates a new client.
   *
   * @param request the client creation request
   * @return the created client response
   */
  @PostMapping
  @Operation(summary = "Registrar nuevo cliente",
      description = "Creates a new client with the provided information")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Customer successfully created"),
          @ApiResponse(responseCode = "400", description = "Invalid data"),
          @ApiResponse(responseCode = "409", description = "Email or DNI already exists"),
          @ApiResponse(responseCode = "500", description = "Internal server error")})
  public ResponseEntity<ClientResponse> createClient(
      @Valid @RequestBody CreateClientRequest request) {
    log.info("Received request to create new client");
    ClientResponse response = clientService.createClient(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves all clients.
   *
   * @return list of all clients
   */
  @GetMapping
  @Operation(summary = "Listar todos los clientes",
      description = "Retrieves all clients from the database")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of clients"),
      @ApiResponse(responseCode = "500", description = "Internal server error")})
  public ResponseEntity<List<ClientResponse>> getAllClients() {
    log.info("Received request to get all clients");
    List<ClientResponse> clients = clientService.getAllClients();
    return ResponseEntity.ok(clients);
  }

  /**
   * Retrieves a client by ID.
   *
   * @param id the client ID
   * @return the client if found
   */
  @GetMapping("/{id}")
  @Operation(summary = "Obtener cliente por ID",
      description = "Retrieves a specific client by their ID")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Client found"),
      @ApiResponse(responseCode = "404", description = "Client not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")})
  public ResponseEntity<ClientResponse> getClientById(
      @Parameter(description = "Client ID", required = true) @PathVariable String id) {
    log.info("Received request to get client with id: {}", id);
    ClientResponse client = clientService.getClientById(id);
    return ResponseEntity.ok(client);
  }

  /**
   * Updates a client completely (PUT operation).
   *
   * @param id the client ID to update
   * @param request the update request
   * @return the updated client
   */
  @PutMapping("/{id}")
  @Operation(summary = "Actualizar cliente por ID", description = "Updates all fields of a client")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Client successfully updated"),
          @ApiResponse(responseCode = "400", description = "Invalid data"),
          @ApiResponse(responseCode = "404", description = "Client not found"),
          @ApiResponse(responseCode = "409", description = "Email already in use"),
          @ApiResponse(responseCode = "500", description = "Internal server error")})
  public ResponseEntity<ClientResponse> updateClient(
      @Parameter(description = "Client ID", required = true) @PathVariable String id,
      @Valid @RequestBody UpdateClientRequest request) {
    log.info("Received request to update client with id: {}", id);
    ClientResponse response = clientService.updateClient(id, request);
    return ResponseEntity.ok(response);
  }

  /**
   * Updates a client partially (PATCH operation).
   *
   * @param id the client ID to update
   * @param request the patch request
   * @return the updated client
   */
  @PatchMapping("/{id}")
  @Operation(summary = "Actualizar parcialmente cliente",
      description = "Updates specific fields of a client")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Client successfully patched"),
          @ApiResponse(responseCode = "400", description = "Invalid data"),
          @ApiResponse(responseCode = "404", description = "Client not found"),
          @ApiResponse(responseCode = "409", description = "Email or DNI already in use"),
          @ApiResponse(responseCode = "500", description = "Internal server error")})
  public ResponseEntity<ClientResponse> patchClient(
      @Parameter(description = "Client ID", required = true) @PathVariable String id,
      @Valid @RequestBody PatchClientRequest request) {
    log.info("Received request to patch client with id: {}", id);
    ClientResponse response = clientService.patchClient(id, request);
    return ResponseEntity.ok(response);
  }

  /**
   * Deletes a client by ID.
   *
   * @param id the client ID to delete
   * @return no content response
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Eliminar cliente por ID",
      description = "Deletes a client from the database")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Client deleted"),
      @ApiResponse(responseCode = "404", description = "Client not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")})
  public ResponseEntity<Void> deleteClient(
      @Parameter(description = "Client ID", required = true) @PathVariable String id) {
    log.info("Received request to delete client with id: {}", id);
    clientService.deleteClient(id);
    return ResponseEntity.noContent().build();
  }
}
