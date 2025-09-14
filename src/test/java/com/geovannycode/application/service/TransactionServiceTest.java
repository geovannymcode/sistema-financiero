package com.geovannycode.application.service;

import com.geovannycode.application.dto.CreateTransactionDTO;
import com.geovannycode.application.dto.TransactionDTO;
import com.geovannycode.application.mapper.TransactionMapper;
import com.geovannycode.domain.exception.InvalidAccountOperationException;
import com.geovannycode.domain.model.Account;
import com.geovannycode.domain.model.Transaction;
import com.geovannycode.domain.model.enums.AccountStatus;
import com.geovannycode.domain.model.enums.AccountType;
import com.geovannycode.domain.model.enums.TransactionType;
import com.geovannycode.domain.port.out.AccountPort;
import com.geovannycode.domain.port.out.TransactionPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionPort transactionPort;

    @Mock
    private AccountPort accountPort;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    private Account sourceAccount;
    private Account destinationAccount;
    private Transaction transaction;
    private TransactionDTO transactionDTO;
    private CreateTransactionDTO createTransactionDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        sourceAccount = Account.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .accountNumber("5312345678")
                .status(AccountStatus.ACTIVE)
                .balance(new BigDecimal("1000.00"))
                .gmfExempt(false)
                .build();

        destinationAccount = Account.builder()
                .id(2L)
                .accountType(AccountType.CHECKING)
                .accountNumber("3312345678")
                .status(AccountStatus.ACTIVE)
                .balance(new BigDecimal("500.00"))
                .gmfExempt(false)
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .transactionType(TransactionType.TRANSFER)
                .amount(new BigDecimal("100.00"))
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .build();

        transactionDTO = TransactionDTO.builder()
                .id(1L)
                .transactionType(TransactionType.TRANSFER)
                .amount(new BigDecimal("100.00"))
                .sourceAccountNumber("5312345678")
                .destinationAccountNumber("3312345678")
                .transactionDate(LocalDateTime.now())
                .build();

        createTransactionDTO = CreateTransactionDTO.builder()
                .transactionType(TransactionType.TRANSFER)
                .amount(new BigDecimal("100.00"))
                .sourceAccountNumber("5312345678")
                .destinationAccountNumber("3312345678")
                .build();
    }

    @Test
    void createTransaction_Transfer_Success() {
        // Given
        when(accountPort.findByAccountNumber("5312345678")).thenReturn(Optional.of(sourceAccount));
        when(accountPort.findByAccountNumber("3312345678")).thenReturn(Optional.of(destinationAccount));
        when(transactionPort.saveTransaction(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(transactionDTO);

        // When
        TransactionDTO result = transactionService.createTransaction(createTransactionDTO);

        // Then
        assertNotNull(result);
        assertEquals(transactionDTO.getId(), result.getId());
        assertEquals(transactionDTO.getTransactionType(), result.getTransactionType());
        verify(transactionPort).saveTransaction(any(Transaction.class));
    }

    @Test
    void createDeposit_Success() {
        // Given
        when(accountPort.findByAccountNumber(any())).thenReturn(Optional.of(destinationAccount));
        when(transactionPort.saveTransaction(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(transactionDTO);

        // When
        TransactionDTO result = transactionService.createDeposit("3312345678", new BigDecimal("100.00"));

        // Then
        assertNotNull(result);
        verify(accountPort).saveAccount(any(Account.class));
        verify(transactionPort).saveTransaction(any(Transaction.class));
    }

    @Test
    void createWithdrawal_Success() {
        // Given
        when(accountPort.findByAccountNumber(any())).thenReturn(Optional.of(sourceAccount));
        when(transactionPort.saveTransaction(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(transactionDTO);

        // When
        TransactionDTO result = transactionService.createWithdrawal("5312345678", new BigDecimal("100.00"));

        // Then
        assertNotNull(result);
        verify(accountPort).saveAccount(any(Account.class));
        verify(transactionPort).saveTransaction(any(Transaction.class));
    }

    @Test
    void createTransfer_Success() {
        // Given
        when(accountPort.findByAccountNumber("5312345678")).thenReturn(Optional.of(sourceAccount));
        when(accountPort.findByAccountNumber("3312345678")).thenReturn(Optional.of(destinationAccount));
        when(transactionPort.saveTransaction(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(transactionDTO);

        // When
        TransactionDTO result = transactionService.createTransfer(
                "5312345678", "3312345678", new BigDecimal("100.00"));

        // Then
        assertNotNull(result);
        verify(accountPort).saveAccount(sourceAccount);
        verify(accountPort).saveAccount(destinationAccount);
        verify(transactionPort).saveTransaction(any(Transaction.class));
    }

    @Test
    void createWithdrawal_InsufficientFunds_ThrowsException() {
        // Given
        when(accountPort.findByAccountNumber(any())).thenReturn(Optional.of(sourceAccount));

        // When & Then
        assertThrows(InvalidAccountOperationException.class, () ->
                transactionService.createWithdrawal("5312345678", new BigDecimal("2000.00"))
        );
    }

    @Test
    void createWithdrawal_InactiveAccount_ThrowsException() {
        // Given
        Account inactiveAccount = Account.builder()
                .id(1L)
                .accountNumber("5312345678")
                .status(AccountStatus.INACTIVE)
                .balance(new BigDecimal("1000.00"))
                .build();

        when(accountPort.findByAccountNumber(any())).thenReturn(Optional.of(inactiveAccount));

        // When & Then
        assertThrows(InvalidAccountOperationException.class, () ->
                transactionService.createWithdrawal("5312345678", new BigDecimal("100.00"))
        );
    }

    @Test
    void findTransactionById_Success() {
        // Given
        when(transactionPort.findById(anyLong())).thenReturn(Optional.of(transaction));
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(transactionDTO);

        // When
        Optional<TransactionDTO> result = transactionService.findTransactionById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(transactionDTO.getId(), result.get().getId());
    }

    @Test
    void getTransactionsByAccount_Success() {
        // Given
        List<Transaction> transactions = Arrays.asList(transaction);
        when(accountPort.findByAccountNumber(any())).thenReturn(Optional.of(sourceAccount));
        when(transactionPort.findByAccountId(anyLong())).thenReturn(transactions);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(transactionDTO);

        // When
        List<TransactionDTO> result = transactionService.getTransactionsByAccount("5312345678");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(transactionDTO.getId(), result.get(0).getId());
    }
}
