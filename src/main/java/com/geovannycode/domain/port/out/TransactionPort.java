package com.geovannycode.domain.port.out;

import com.geovannycode.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionPort {
    Transaction saveTransaction(Transaction transaction);
    Optional<Transaction> findById(Long id);
    List<Transaction> findByAccountId(Long accountId);
}
