package com.bootcamp.transactions.repository;

import com.bootcamp.transactions.domain.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Repositorio reactivo para operaciones con la entidad Transaction.
 */
public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {

    /**
     * Busca transacciones por ID de cuenta origen o destino.
     *
     * @param sourceAccountId ID de la cuenta origen
     * @param targetAccountId ID de la cuenta destino
     * @return Flux de transacciones encontradas
     */
    Flux<Transaction> findBySourceAccountIdOrTargetAccountId(String sourceAccountId, String targetAccountId);
}