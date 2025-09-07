package com.bootcamp.transactions.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank String cuentaOrigen,
        @NotBlank String cuentaDestino,
        @NotNull @DecimalMin("0.01") BigDecimal monto
) {}
