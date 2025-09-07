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
        if (r.getAmount()==null || r.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("amount debe ser > 0"));
        }
        return save("DEPOSITO", r.getAccountId(), null, r.getAmount(), r.getCurrency(), "APLICADA", null)
                .map(this::toResponse);
    }

    public Mono<TransactionResponse> withdraw(WithdrawRequest r) {
        if (r.getAmount()==null || r.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("amount debe ser > 0"));
        }
        // Hook para validar saldo con account-ms (ejemplo)
        // WebClient webClient = WebClient.create("http://localhost:8082");
        // Mono<Account> acc = webClient.get().uri("/accounts/{id}", r.getAccountId()).retrieve().bodyToMono(Account.class);
        // return acc.flatMap(a -> a.getBalance().compareTo(r.getAmount()) < 0 ? Mono.error(new IllegalArgumentException("Saldo insuficiente")) :
        //        save("RETIRO", r.getAccountId(), null, r.getAmount(), r.getCurrency(), "APLICADA", null).map(this::toResponse));
        return save("RETIRO", r.getAccountId(), null, r.getAmount(), r.getCurrency(), "APLICADA", null).map(this::toResponse);
    }

    public Mono<TransactionResponse> transfer(TransferRequest r) {
        if (r.getAmount()==null || r.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("amount debe ser > 0"));
        }
        if (r.getSourceAccountId().equals(r.getTargetAccountId())) {
            return Mono.error(new IllegalArgumentException("source y target no pueden ser iguales"));
        }
        return save("TRANSFERENCIA", r.getSourceAccountId(), r.getTargetAccountId(), r.getAmount(), r.getCurrency(), "APLICADA", null)
                .map(this::toResponse);
    }

    public Flux<TransactionResponse> history(String accountId, Instant from, Instant to) {
        if (from == null) from = Instant.EPOCH;
        if (to == null) to = Instant.now();
        return repository.findBySourceAccountIdOrTargetAccountIdAndDateBetween(accountId, accountId, from, to)
                .map(this::toResponse);
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId()).type(t.getType()).amount(t.getAmount()).currency(t.getCurrency())
                .date(t.getDate()).sourceAccountId(t.getSourceAccountId()).targetAccountId(t.getTargetAccountId())
                .status(t.getStatus()).reason(t.getReason()).build();
    }
}
