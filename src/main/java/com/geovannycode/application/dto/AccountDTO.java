package com.geovannycode.application.dto;

import com.geovannycode.domain.model.enums.AccountStatus;
import com.geovannycode.domain.model.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode
public class AccountDTO {

    private final Long id;
    @NotNull(message = "Account type is required")
    private final AccountType accountType;
    private final String accountNumber;
    private final AccountStatus status;
    private final BigDecimal balance;
    private final Boolean gmfExempt;
    private final Long customerId;
    private final String customerName;

    @Builder
    public AccountDTO(Long id, AccountType accountType, String accountNumber,
                      AccountStatus status, BigDecimal balance, Boolean gmfExempt,
                      Long customerId, String customerName) {
        this.id = id;
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.status = status;
        this.balance = balance;
        this.gmfExempt = gmfExempt;
        this.customerId = customerId;
        this.customerName = customerName;
    }
}
