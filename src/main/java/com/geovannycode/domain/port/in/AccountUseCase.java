package com.geovannycode.domain.port.in;

import com.geovannycode.application.dto.AccountDTO;
import com.geovannycode.domain.model.enums.AccountStatus;

import java.util.List;
import java.util.Optional;

public interface AccountUseCase {
    AccountDTO createAccount(AccountDTO accountDTO, Long customerId);
    AccountDTO changeStatus(Long id, AccountStatus status);
    void cancelAccount(Long id);
    Optional<AccountDTO> findAccountById(Long id);
    Optional<AccountDTO> findAccountByNumber(String accountNumber);
    List<AccountDTO> getAccountsByCustomerId(Long customerId);
}
