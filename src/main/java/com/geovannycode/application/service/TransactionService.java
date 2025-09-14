package com.geovannycode.application.service;

import com.geovannycode.application.dto.CreateTransactionDTO;
import com.geovannycode.application.dto.TransactionDTO;
import com.geovannycode.application.mapper.TransactionMapper;
import com.geovannycode.domain.exception.InvalidAccountOperationException;
import com.geovannycode.domain.exception.ResourceNotFoundException;
import com.geovannycode.domain.model.Account;
import com.geovannycode.domain.model.Transaction;
import com.geovannycode.domain.model.enums.AccountStatus;
import com.geovannycode.domain.model.enums.TransactionType;
import com.geovannycode.domain.port.in.TransactionUseCase;
import com.geovannycode.domain.port.out.AccountPort;
import com.geovannycode.domain.port.out.TransactionPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService implements TransactionUseCase {

    private final TransactionPort transactionPort;
    private final AccountPort accountPort;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionDTO createTransaction(CreateTransactionDTO transactionDTO) {
        switch (transactionDTO.getTransactionType()) {
            case DEPOSIT:
                return createDeposit(
                        transactionDTO.getDestinationAccountNumber(),
                        transactionDTO.getAmount());
            case WITHDRAWAL:
                return createWithdrawal(
                        transactionDTO.getSourceAccountNumber(),
                        transactionDTO.getAmount());
            case TRANSFER:
                return createTransfer(
                        transactionDTO.getSourceAccountNumber(),
                        transactionDTO.getDestinationAccountNumber(),
                        transactionDTO.getAmount());
            default:
                throw new IllegalArgumentException("Invalid transaction type");
        }
    }

    @Override
    @Transactional
    public TransactionDTO createDeposit(String accountNumber, BigDecimal amount) {

        Account account = accountPort.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        validateAccountIsActive(account);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.DEPOSIT)
                .amount(amount)
                .destinationAccount(account)
                .build();

        account.updateBalance(amount);
        accountPort.saveAccount(account);

        Transaction savedTransaction = transactionPort.saveTransaction(transaction);
        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionDTO createWithdrawal(String accountNumber, BigDecimal amount) {

        Account account = accountPort.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        validateAccountIsActive(account);
        validateSufficientFunds(account, amount);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(amount)
                .sourceAccount(account)
                .build();

        account.updateBalance(amount.negate());
        accountPort.saveAccount(account);

        Transaction savedTransaction = transactionPort.saveTransaction(transaction);
        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionDTO createTransfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {

        Account sourceAccount = accountPort.findByAccountNumber(sourceAccountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found: " + sourceAccountNumber));

        Account destinationAccount = accountPort.findByAccountNumber(destinationAccountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found: " + destinationAccountNumber));

        validateAccountIsActive(sourceAccount);
        validateAccountIsActive(destinationAccount);
        validateSufficientFunds(sourceAccount, amount);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.TRANSFER)
                .amount(amount)
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .build();

        sourceAccount.updateBalance(amount.negate());
        destinationAccount.updateBalance(amount);

        accountPort.saveAccount(sourceAccount);
        accountPort.saveAccount(destinationAccount);

        Transaction savedTransaction = transactionPort.saveTransaction(transaction);
        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    @Transactional
    public Optional<TransactionDTO> findTransactionById(Long id) {
        return transactionPort.findById(id)
                .map(transactionMapper::toDTO);
    }

    @Override
    @Transactional
    public List<TransactionDTO> getTransactionsByAccount(String accountNumber) {
        Optional<Account> accountOpt = accountPort.findByAccountNumber(accountNumber);

        if (accountOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Account account = accountOpt.get();
        List<Transaction> transactions = transactionPort.findByAccountId(account.getId());

        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    private void validateAccountIsActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountOperationException("Account is not active: " + account.getAccountNumber());
        }
    }

    private void validateSufficientFunds(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InvalidAccountOperationException("Insufficient funds in account: " + account.getAccountNumber());
        }
    }
}
