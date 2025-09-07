package com.bootcamp.transactions.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {
    @NotBlank(message = "El ID de la cuenta es obligatorio")
    private String accountId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal amount;

    @NotBlank(message = "La moneda es obligatoria")
    private String currency;
}