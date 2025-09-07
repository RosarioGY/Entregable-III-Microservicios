package com.bootcamp.transactions.controller;

import com.bootcamp.transactions.domain.Transaction;
import com.bootcamp.transactions.dto.*;
import com.bootcamp.transactions.exception.BusinessException;
import com.bootcamp.transactions.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transacciones")
public class TransactionController {

    private final TransactionService service;

    @PostMapping("/deposito")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Transaction> depositar(@Valid @RequestBody DepositRequest req) {
        return service.deposit(req);
    }

    @PostMapping("/retiro")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Transaction> retirar(@Valid @RequestBody WithdrawalRequest req) {
        return service.withdraw(req);
    }

    @PostMapping("/transferencia")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Transaction> transferir(@Valid @RequestBody TransferRequest req) {
        return service.transfer(req);
    }

    @GetMapping("/historial")
    public Flux<Transaction> historial(@RequestParam(value = "cuenta", required = false) String cuenta) {
        return service.history(cuenta);
    }

    /* Manejo simple de errores de negocio */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public Mono<String> onBusinessError(BusinessException ex) {
        return Mono.just(ex.getMessage());
    }
}
