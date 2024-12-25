package com.kuro.expensetracker.exceptions;

public class UserAlreadyPresentException extends EntityAlreadyPresentException {
    public UserAlreadyPresentException(String name) {
        super("User with credential [" + name + "] is already present.");
    }
}
