package com.geovannycode.infrastructure.rest;

import com.geovannycode.application.dto.AccountDTO;
import com.geovannycode.domain.model.enums.AccountStatus;
import com.geovannycode.domain.port.in.AccountUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountUseCase accountUseCase;


    @PostMapping("/customers/{customerId}")
    public ResponseEntity<AccountDTO> createAccount(
            @PathVariable Long customerId,
            @Valid @RequestBody AccountDTO accountDTO) {
        return new ResponseEntity<>(accountUseCase.createAccount(accountDTO, customerId), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AccountDTO> changeStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status) {
        return ResponseEntity.ok(accountUseCase.changeStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAccount(@PathVariable Long id) {
        accountUseCase.cancelAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        return accountUseCase.findAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountDTO> getAccountByNumber(@PathVariable String accountNumber) {
        return accountUseCase.findAccountByNumber(accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountUseCase.getAccountsByCustomerId(customerId));
    }
}
