package com.kuro.expensetracker.responses;

import com.kuro.expensetracker.models.User;

public record AuthResponse(String token, User user) {
}
