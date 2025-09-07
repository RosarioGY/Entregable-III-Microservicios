package com.bootcamp.transactions.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String type; // DEPOSITO, RETIRO, TRANSFERENCIA
    private BigDecimal amount;
    private String currency;
    private Instant date;
    private String sourceAccountId;
    private String targetAccountId;
    private String status;
    private String reason;
}
