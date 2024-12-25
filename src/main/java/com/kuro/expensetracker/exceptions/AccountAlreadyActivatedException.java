package com.kuro.expensetracker.exceptions;

public class AccountAlreadyActivatedException extends RuntimeException {
    public AccountAlreadyActivatedException() {
        super("Your account is already activated. You can log in with your credentials.");
    }
}