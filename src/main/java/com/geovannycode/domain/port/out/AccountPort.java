package com.geovannycode.domain.port.out;


import com.geovannycode.domain.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountPort {

    Account saveAccount(Account account);
    Optional<Account> findById(Long id);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByCustomerId(Long customerId);
}
