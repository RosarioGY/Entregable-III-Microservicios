package com.bootcamp.transactions.api;

import com.bootcamp.transactions.domain.Transaction;
import com.bootcamp.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transacciones")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @PostMapping("/deposito")
    public Mono<Transaction> deposit(@RequestBody DepositRequest request) {
        return service.deposit(request);
    }
}
