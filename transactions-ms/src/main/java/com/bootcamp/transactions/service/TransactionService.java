package com.bootcamp.transactions.service;

import com.bootcamp.transactions.api.DepositRequest;
import com.bootcamp.transactions.domain.Transaction;
import com.bootcamp.transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;

    public Mono<Transaction> deposit(DepositRequest request) {
        Transaction tx = Transaction.builder()
                .type("DEPOSITO")
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .date(Instant.now())
                .sourceAccountId(request.getAccountId())
                .status("APLICADA")
                .build();
        return repository.save(tx);
    }
}
