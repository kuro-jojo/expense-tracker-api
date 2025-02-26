package com.kuro.expensetracker.requests;

public record VerifyOtpRequest(Long userID, String email, String otp) {
}
