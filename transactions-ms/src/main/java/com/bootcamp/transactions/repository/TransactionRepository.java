package com.bootcamp.transactions.repository;

import com.bootcamp.transactions.domain.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
    Flux<Transaction> findBySourceAccountIdOrTargetAccountId(String sourceId, String targetId);
}
