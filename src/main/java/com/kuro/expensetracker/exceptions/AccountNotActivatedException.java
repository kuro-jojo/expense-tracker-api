package com.kuro.expensetracker.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountNotActivatedException extends RuntimeException {
    private String sessionID;

    public AccountNotActivatedException(String sessionID) {
        super("Email not activated yet. A confirmation email was resend");
        this.sessionID = sessionID;
    }
}
