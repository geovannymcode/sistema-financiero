package com.geovannycode.application.service;

import com.geovannycode.application.dto.AccountDTO;
import com.geovannycode.application.mapper.AccountMapper;
import com.geovannycode.domain.exception.InvalidAccountOperationException;
import com.geovannycode.domain.exception.ResourceNotFoundException;
import com.geovannycode.domain.model.Account;
import com.geovannycode.domain.model.Customer;
import com.geovannycode.domain.model.enums.AccountStatus;
import com.geovannycode.domain.port.in.AccountUseCase;
import com.geovannycode.domain.port.out.AccountPort;
import com.geovannycode.domain.port.out.CustomerPort;
import com.geovannycode.infrastructure.util.AccountNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountUseCase {

    private final AccountPort accountPort;
    private final CustomerPort customerPort;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountDTO createAccount(AccountDTO accountDTO, Long customerId) {

        Customer customer = customerPort.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        String accountNumber = accountNumberGenerator.generate(accountDTO.getAccountType());

        Account newAccount = Account.builder()
                .accountType(accountDTO.getAccountType())
                .accountNumber(accountNumber)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .gmfExempt(accountDTO.getGmfExempt() != null ? accountDTO.getGmfExempt() : false)
                .customer(customer)
                .build();

        Account savedAccount = accountPort.saveAccount(newAccount);
        return accountMapper.toDTO(savedAccount);
    }

    @Override
    @Transactional
    public AccountDTO changeStatus(Long id, AccountStatus status) {
        Account account = accountPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

        if (status == AccountStatus.CANCELLED && !account.getBalance().equals(BigDecimal.ZERO)) {
            throw new InvalidAccountOperationException("Cannot cancel account with non-zero balance");
        }

        account.setStatus(status);
        Account updatedAccount = accountPort.saveAccount(account);
        return accountMapper.toDTO(updatedAccount);
    }

    @Override
    @Transactional
    public void cancelAccount(Long id) {
        Account account = accountPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

        if (!account.getBalance().equals(BigDecimal.ZERO)) {
            throw new InvalidAccountOperationException("Cannot cancel account with non-zero balance");
        }

        account.setStatus(AccountStatus.CANCELLED);
        accountPort.saveAccount(account);
    }

    @Override
    @Transactional
    public Optional<AccountDTO> findAccountById(Long id) {
        return accountPort.findById(id)
                .map(accountMapper::toDTO);
    }

    @Override
    @Transactional
    public Optional<AccountDTO> findAccountByNumber(String accountNumber) {
        return accountPort.findByAccountNumber(accountNumber)
                .map(accountMapper::toDTO);
    }

    @Override
    @Transactional
    public List<AccountDTO> getAccountsByCustomerId(Long customerId) {
        List<Account> accounts = accountPort.findByCustomerId(customerId);
        return accounts.stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }
}
