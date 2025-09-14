package com.geovannycode.infrastructure.rest;

import com.geovannycode.application.dto.CreateTransactionDTO;
import com.geovannycode.application.dto.TransactionDTO;
import com.geovannycode.domain.port.in.TransactionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionUseCase transactionUseCase;

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody CreateTransactionDTO transactionDTO) {
        return new ResponseEntity<>(transactionUseCase.createTransaction(transactionDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        return transactionUseCase.findTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionUseCase.getTransactionsByAccount(accountNumber));
    }
}
