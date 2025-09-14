package com.geovannycode.infrastructure.persistence.adapter;

import com.geovannycode.domain.model.Transaction;
import com.geovannycode.domain.port.out.TransactionPort;
import com.geovannycode.infrastructure.persistence.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class TransactionAdapter implements TransactionPort {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<Transaction> findByAccountId(Long accountId) {
        List<Transaction> sourceTransactions = transactionRepository.findBySourceAccountId(accountId);
        List<Transaction> destinationTransactions = transactionRepository.findByDestinationAccountId(accountId);

        return Stream.concat(sourceTransactions.stream(), destinationTransactions.stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
