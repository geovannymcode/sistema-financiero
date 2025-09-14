package com.geovannycode.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.geovannycode.application.dto.AccountDTO;
import com.geovannycode.application.mapper.AccountMapper;
import com.geovannycode.domain.exception.InvalidAccountOperationException;
import com.geovannycode.domain.exception.ResourceNotFoundException;
import com.geovannycode.domain.model.Account;
import com.geovannycode.domain.model.Customer;
import com.geovannycode.domain.model.enums.AccountStatus;
import com.geovannycode.domain.model.enums.AccountType;
import com.geovannycode.domain.port.out.AccountPort;
import com.geovannycode.domain.port.out.CustomerPort;
import com.geovannycode.infrastructure.util.AccountNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountPort accountPort;

    @Mock
    private CustomerPort customerPort;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    private Customer customer;
    private Account account;
    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        customer = Customer.builder()
                .id(1L)
                .identificationType("CC")
                .identificationNumber("123456789")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .birthDate(LocalDate.now().minusYears(25))
                .build();

        account = Account.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .accountNumber("5312345678")
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .gmfExempt(false)
                .customer(customer)
                .build();

        accountDTO = AccountDTO.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .accountNumber("5312345678")
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .gmfExempt(false)
                .customerId(1L)
                .customerName("John Doe")
                .build();
    }

    @Test
    void createAccount_Success() {
        // Given
        when(customerPort.findById(anyLong())).thenReturn(Optional.of(customer));
        when(accountNumberGenerator.generate(any(AccountType.class))).thenReturn("5312345678");
        when(accountPort.saveAccount(any(Account.class))).thenReturn(account);
        when(accountMapper.toDTO(any(Account.class))).thenReturn(accountDTO);

        // When
        AccountDTO result = accountService.createAccount(accountDTO, 1L);

        // Then
        assertNotNull(result);
        assertEquals(accountDTO.getId(), result.getId());
        assertEquals(accountDTO.getAccountNumber(), result.getAccountNumber());
        verify(accountPort).saveAccount(any(Account.class));
    }

    @Test
    void createAccount_CustomerNotFound_ThrowsException() {
        // Given
        when(customerPort.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
                accountService.createAccount(accountDTO, 1L)
        );
    }

    @Test
    void changeStatus_Success() {
        // Given
        when(accountPort.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountPort.saveAccount(any(Account.class))).thenReturn(account);
        when(accountMapper.toDTO(any(Account.class))).thenReturn(accountDTO);

        // When
        AccountDTO result = accountService.changeStatus(1L, AccountStatus.INACTIVE);

        // Then
        assertNotNull(result);
        verify(accountPort).saveAccount(any(Account.class));
    }

    @Test
    void changeStatus_AccountNotFound_ThrowsException() {
        // Given
        when(accountPort.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
                accountService.changeStatus(1L, AccountStatus.INACTIVE)
        );
    }

    @Test
    void cancelAccount_WithZeroBalance_Success() {
        // Given
        when(accountPort.findById(anyLong())).thenReturn(Optional.of(account));

        // When
        accountService.cancelAccount(1L);

        // Then
        verify(accountPort).saveAccount(any(Account.class));
    }

    @Test
    void cancelAccount_WithNonZeroBalance_ThrowsException() {
        // Given
        Account accountWithBalance = Account.builder()
                .id(1L)
                .balance(new BigDecimal("100.00"))
                .build();

        when(accountPort.findById(anyLong())).thenReturn(Optional.of(accountWithBalance));

        // When & Then
        assertThrows(InvalidAccountOperationException.class, () ->
                accountService.cancelAccount(1L)
        );
    }

    @Test
    void findAccountById_Success() {
        // Given
        when(accountPort.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountMapper.toDTO(any(Account.class))).thenReturn(accountDTO);

        // When
        Optional<AccountDTO> result = accountService.findAccountById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(accountDTO.getId(), result.get().getId());
    }

    @Test
    void findAccountByNumber_Success() {
        // Given
        when(accountPort.findByAccountNumber(any())).thenReturn(Optional.of(account));
        when(accountMapper.toDTO(any(Account.class))).thenReturn(accountDTO);

        // When
        Optional<AccountDTO> result = accountService.findAccountByNumber("5312345678");

        // Then
        assertTrue(result.isPresent());
        assertEquals(accountDTO.getAccountNumber(), result.get().getAccountNumber());
    }

    @Test
    void getAccountsByCustomerId_Success() {
        // Given
        List<Account> accounts = Arrays.asList(account);
        when(accountPort.findByCustomerId(anyLong())).thenReturn(accounts);
        when(accountMapper.toDTO(any(Account.class))).thenReturn(accountDTO);

        // When
        List<AccountDTO> result = accountService.getAccountsByCustomerId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(accountDTO.getId(), result.get(0).getId());
    }
}
