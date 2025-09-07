package com.bootcamp.transactions.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("TransactionService Integration Tests")
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    @DisplayName("Context loads and TransactionService bean is available")
    void contextLoads() {
        assertNotNull(transactionService, "TransactionService bean should be loaded");
    }
}