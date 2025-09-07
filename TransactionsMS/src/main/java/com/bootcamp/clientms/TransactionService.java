package com.bootcamp.transactions.service;

import com.bootcamp.transactions.domain.*;
import com.bootcamp.transactions.dto.*;
import com.bootcamp.transactions.exception.BusinessException;
import com.bootcamp.transactions.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;
    private final ReactiveMongoTemplate mongo;

    public Mono<Transaction> deposit(DepositRequest req) {
        // Crea la cuenta si no existe y suma el monto
        return ensureAccount(req.cuenta())
                .flatMap(acc -> incBalance(acc.getId(), req.monto()))
                .flatMap(acc -> saveTx(TransactionType.DEPOSITO, req.monto(), req.cuenta(), null));
    }

    public Mono<Transaction> withdraw(WithdrawalRequest req) {
        return decBalanceIfEnough(req.cuenta(), req.monto())
                .switchIfEmpty(Mono.error(new BusinessException("Saldo insuficiente")))
                .flatMap(acc -> saveTx(TransactionType.RETIRO, req.monto(), req.cuenta(), null));
    }

    public Mono<Transaction> transfer(TransferRequest req) {
        if (req.cuentaOrigen().equals(req.cuentaDestino())) {
            return Mono.error(new BusinessException("La cuenta de origen y destino no pueden ser iguales"));
        }
        // 1) Debita origen si hay saldo suficiente (operación atómica)
        return decBalanceIfEnough(req.cuentaOrigen(), req.monto())
                .switchIfEmpty(Mono.error(new BusinessException("Saldo insuficiente")))
                // 2) Asegura cuenta destino y acredita
                .flatMap(a -> ensureAccount(req.cuentaDestino()))
                .flatMap(a -> incBalance(req.cuentaDestino(), req.monto()))
                // 3) Registra transacción
                .flatMap(a -> saveTx(TransactionType.TRANSFERENCIA, req.monto(), req.cuentaOrigen(), req.cuentaDestino()));
        // Nota: para garantizar atomicidad entre documentos se necesitaría transacción Mongo (replica set).
    }

    public Flux<Transaction> history(String cuenta) {
        if (cuenta == null || cuenta.isBlank()) {
            return txRepo.findAll().sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        }
        // Uso de flujos reactivos (Streams) para filtrar por cuenta
        return txRepo.findBySourceAccountOrDestAccountOrderByDateDesc(cuenta, cuenta);
    }

    /* Helpers */

    private Mono<Account> ensureAccount(String id) {
        return accountRepo.findById(id)
                .switchIfEmpty(accountRepo.save(new Account(id, BigDecimal.ZERO)));
    }

    private Mono<Account> incBalance(String id, BigDecimal amount) {
        Query q = Query.query(where("_id").is(id));
        Update u = new Update().inc("balance", amount);
        return mongo.findAndModify(q, u, FindAndModifyOptions.options().returnNew(true), Account.class);
    }

    private Mono<Account> decBalanceIfEnough(String id, BigDecimal amount) {
        // Disminuye solo si balance >= amount (chequeo + update atómico)
        Query q = Query.query(where("_id").is(id).and("balance").gte(amount));
        Update u = new Update().inc("balance", amount.negate());
        return mongo.findAndModify(q, u, FindAndModifyOptions.options().returnNew(true), Account.class);
    }

    private Mono<Transaction> saveTx(TransactionType type, BigDecimal amt, String source, String dest) {
        Transaction t = Transaction.builder()
                .type(type)
                .amount(amt)
                .date(Instant.now())
                .sourceAccount(source)
                .destAccount(dest)
                .build();
        return txRepo.save(t);
    }
}
