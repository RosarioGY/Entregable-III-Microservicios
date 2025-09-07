package com.bootcamp.clientms.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchClientRequest {

  private String firstName;
  private String lastName;
  private String dni;
  @Email(message = "Email must be valid")
  private String email;
}
