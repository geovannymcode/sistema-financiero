package com.geovannycode.infrastructure.util;

import com.geovannycode.domain.model.enums.AccountType;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;


@Component
public class AccountNumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String SAVINGS_PREFIX = "53";
    private static final String CHECKING_PREFIX = "33";

    public String generate(AccountType accountType) {
        String prefix = accountType == AccountType.SAVINGS ? SAVINGS_PREFIX : CHECKING_PREFIX;
        StringBuilder sb = new StringBuilder(prefix);

        for (int i = 0; i < 8; i++) {
            sb.append(RANDOM.nextInt(10));
        }

        return sb.toString();
    }
}
