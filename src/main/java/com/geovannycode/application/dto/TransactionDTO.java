package com.geovannycode.application.dto;

import com.geovannycode.domain.model.enums.TransactionType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
public class TransactionDTO {

    private final Long id;
    private final TransactionType transactionType;
    private final BigDecimal amount;
    private final LocalDateTime transactionDate;
    private final String sourceAccountNumber;
    private final String destinationAccountNumber;

    @Builder
    public TransactionDTO(Long id, TransactionType transactionType, BigDecimal amount,
                          LocalDateTime transactionDate, String sourceAccountNumber,
                          String destinationAccountNumber) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
    }
}
