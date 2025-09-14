package com.geovannycode.application.mapper;

import com.geovannycode.application.dto.TransactionDTO;
import com.geovannycode.domain.model.Account;
import com.geovannycode.domain.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {

    public TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        Account sourceAccount = transaction.getSourceAccount();
        Account destinationAccount = transaction.getDestinationAccount();

        return TransactionDTO.builder()
                .id(transaction.getId())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .sourceAccountNumber(sourceAccount != null ? sourceAccount.getAccountNumber() : null)
                .destinationAccountNumber(destinationAccount != null ? destinationAccount.getAccountNumber() : null)
                .build();
    }

    public List<TransactionDTO> toDTOList(List<Transaction> transactions) {
        if (transactions == null) {
            return null;
        }

        return transactions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
