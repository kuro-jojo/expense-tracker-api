package com.kuro.expensetracker.exceptions;

public class EmailConfirmationException extends RuntimeException {
    public EmailConfirmationException(String message) {
        super(message);
    }
}
