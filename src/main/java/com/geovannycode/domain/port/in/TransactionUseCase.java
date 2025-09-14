package com.geovannycode.domain.port.in;

import com.geovannycode.application.dto.CreateTransactionDTO;
import com.geovannycode.application.dto.TransactionDTO;
import com.geovannycode.domain.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionUseCase {
    TransactionDTO createTransaction(CreateTransactionDTO transactionDTO);
    TransactionDTO createDeposit(String accountNumber, BigDecimal amount);
    TransactionDTO createWithdrawal(String accountNumber, BigDecimal amount);
    TransactionDTO createTransfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount);
    Optional<TransactionDTO> findTransactionById(Long id);
    List<TransactionDTO> getTransactionsByAccount(String accountNumber);
}
