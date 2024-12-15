package com.kuro.expensetracker.requests;

import jakarta.validation.constraints.Max;

public class ExpenseRequest extends TransactionRequest {
    @Max(0)
    private Float amount;
}
