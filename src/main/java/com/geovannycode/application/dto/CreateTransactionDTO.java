package com.geovannycode.application.dto;

import com.geovannycode.domain.model.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode
public class CreateTransactionDTO {

    @NotNull(message = "Transaction type is required")
    private final TransactionType transactionType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private final BigDecimal amount;

    private final String sourceAccountNumber;

    private final String destinationAccountNumber;

    @Builder
    public CreateTransactionDTO(TransactionType transactionType, BigDecimal amount,
                                String sourceAccountNumber, String destinationAccountNumber) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
    }
}
