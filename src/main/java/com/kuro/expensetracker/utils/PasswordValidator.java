package com.kuro.expensetracker.utils;

import jakarta.validation.constraints.NotNull;

public class PasswordValidator {
    public static final String PASSWORD_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
    public static final String PASSWORD_REQUIREMENT = "8 characters, at least 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character.";

    public static boolean isPasswordValid(@NotNull String password) {
        return password.matches(PASSWORD_REGEX);
    }
}
