package com.geovannycode.domain.exception;

public class CustomerHasAccountsException extends RuntimeException {
    public CustomerHasAccountsException(String message) {
        super(message);
    }
}
