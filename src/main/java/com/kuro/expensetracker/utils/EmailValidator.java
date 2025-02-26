package com.kuro.expensetracker.utils;

import jakarta.validation.constraints.NotNull;

public class EmailValidator {
    public static final String EMAIL_REGEX = "[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";

    public static boolean isEmailInvalid(@NotNull String email) {
        return !email.matches(EMAIL_REGEX);
    }
}