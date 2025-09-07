package com.bootcamp.transactions.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document("transactions")
public class Transaction {
    @Id
    private String id;

    private TransactionType type;
    private BigDecimal amount;
    private Instant date;

    private String sourceAccount; // cuenta origen
    private String destAccount;   // cuenta destino (solo transferencias)
}
