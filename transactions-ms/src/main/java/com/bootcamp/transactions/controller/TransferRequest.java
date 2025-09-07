package com.bootcamp.transactions.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    @NotBlank(message = "El ID de la cuenta origen es obligatorio")
    private String sourceAccountId;

    @NotBlank(message = "El ID de la cuenta destino es obligatorio")
    private String targetAccountId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal amount;