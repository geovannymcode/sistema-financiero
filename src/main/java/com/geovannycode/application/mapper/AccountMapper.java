package com.geovannycode.application.mapper;

import com.geovannycode.application.dto.AccountDTO;
import com.geovannycode.domain.model.Account;
import com.geovannycode.domain.model.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {

    public AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }

        Customer customer = account.getCustomer();
        String customerName = customer != null ?
                customer.getFirstName() + " " + customer.getLastName() : null;

        return AccountDTO.builder()
                .id(account.getId())
                .accountType(account.getAccountType())
                .accountNumber(account.getAccountNumber())
                .status(account.getStatus())
                .balance(account.getBalance())
                .gmfExempt(account.getGmfExempt())
                .customerId(customer != null ? customer.getId() : null)
                .customerName(customerName)
                .build();
    }

    public List<AccountDTO> toDTOList(List<Account> accounts) {
        if (accounts == null) {
            return null;
        }

        return accounts.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
