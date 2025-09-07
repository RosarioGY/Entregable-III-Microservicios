package com.bootcamp.transactions.api;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String sourceAccountId;
    private String targetAccountId;
    private BigDecimal amount;
    private String currency;
}
