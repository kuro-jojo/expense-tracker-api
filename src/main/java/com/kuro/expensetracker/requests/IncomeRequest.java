package com.kuro.expensetracker.requests;

import jakarta.validation.constraints.Min;

public class IncomeRequest extends TransactionRequest {
    @Min(0)
    private Float amount;
}
