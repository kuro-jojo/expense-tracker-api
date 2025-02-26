package com.kuro.expensetracker.exceptions;

public class ConfirmationEmailException extends RuntimeException {
    public ConfirmationEmailException(String message) {
        super(message);
    }
}
