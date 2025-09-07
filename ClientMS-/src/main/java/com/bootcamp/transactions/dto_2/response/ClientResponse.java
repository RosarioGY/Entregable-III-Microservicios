package com.bootcamp.transactions.dto_2.response;

import com.bootcamp.transactions.domain.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponse {
  private String id;
  private String firstName;
  private String lastName;
  private String dni;
  private String email;

  public static ClientResponse from(Client client) {
    return new ClientResponse(client.getId(), client.getFirstName(), client.getLastName(),
        client.getDni(), client.getEmail());
  }
}
