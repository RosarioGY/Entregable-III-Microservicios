package com.bootcamp.transactions.repository;

import com.bootcamp.transactions.domain.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.Arrays;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
    Flux<Transaction> findBySourceAccountOrDestAccountOrderByDateDesc(String source, String dest);

    Arrays findAll();
}
