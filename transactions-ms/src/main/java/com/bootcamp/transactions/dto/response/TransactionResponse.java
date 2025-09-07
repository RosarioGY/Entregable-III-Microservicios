package com.bootcamp.transactions.dto.response;

import com.bootcamp.transactions.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private String id;
    private String sourceAccountId;
    private String targetAccountId;
    private Double amount;
    private String date;

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getSourceAccountId(),
            transaction.getTargetAccountId(),
            transaction.getAmount(),
            transaction.getDate()
        );
    }
}