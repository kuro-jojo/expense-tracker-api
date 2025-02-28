package com.kuro.expensetracker.requests;

public record OtpRequest(String sessionID, String otp) {
}
