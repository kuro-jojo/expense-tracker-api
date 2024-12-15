package com.kuro.expensetracker.requests;

import com.kuro.expensetracker.models.Budget;
import com.kuro.expensetracker.models.PaymentMode;
import com.kuro.expensetracker.models.Transaction;
import lombok.Data;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

@Data
public class UserRequest {
    private Long id;
    private String name;
    private String email;
    private String password;
    private LocalDate joinedAt;
    private Currency currency;
    private List<Transaction> transactions;
    private List<Budget> budgets;
    private List<PaymentMode> paymentModes;
}
