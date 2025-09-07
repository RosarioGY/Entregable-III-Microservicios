package com.bootcamp.transactions.repository;

import com.bootcamp.transactions.domain.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {}
