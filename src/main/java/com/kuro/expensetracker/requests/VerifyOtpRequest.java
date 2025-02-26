package com.kuro.expensetracker.requests;

public record VerifyOtpRequest(String sessionID, String otp) {
}
