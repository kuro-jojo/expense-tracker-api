package com.kuro.expensetracker.exceptions;

import lombok.Getter;

@Getter
public class AccountAlreadyActivatedException extends RuntimeException {
    private final String uuid;

    public AccountAlreadyActivatedException(String uuid) {
        super("Your account is already activated. You can log in with your credentials.");
        this.uuid = uuid;
    }
}