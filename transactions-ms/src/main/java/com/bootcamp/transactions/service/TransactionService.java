package com.bootcamp.transactions.service;

import com.bootcamp.transactions.api.*;
import com.bootcamp.transactions.domain.Transaction;
import com.bootcamp.transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;

    private Mono<Transaction> save(String type, String source, String target, BigDecimal amount, String currency, String status, String reason) {
        return repository.save(Transaction.builder()
                .type(type).sourceAccountId(source).targetAccountId(target)
                .amount(amount).currency(currency).date(Instant.now())
                .status(status).reason(reason).build());
    }

    public Mono<TransactionResponse> deposit(DepositRequest r) {
        if (r.getAmount() == null || r.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("amount debe ser > 0"));
        }