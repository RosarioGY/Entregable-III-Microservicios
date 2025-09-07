package com.bootcamp.transactions.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchTransactionRequest {
    private String sourceAccountId;
    private String targetAccountId;

    @Positive(message = "El monto debe ser positivo")
    private Double amount;
}