package com.bootcamp.transactions.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
@Document("accounts")
public class Account {
    @Id
    private String id;          // número de cuenta
    private BigDecimal balance; // saldo disponible
}
