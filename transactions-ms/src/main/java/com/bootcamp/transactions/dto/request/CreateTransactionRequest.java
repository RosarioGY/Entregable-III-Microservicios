package com.bootcamp.transactions.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransactionRequest {
    @NotBlank(message = "El ID de la cuenta origen es obligatorio")
    private String sourceAccountId;

    @NotBlank(message = "El ID de la cuenta destino es obligatorio")
    private String targetAccountId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private Double amount;
}