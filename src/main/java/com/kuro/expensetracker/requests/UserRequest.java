package com.kuro.expensetracker.requests;

import com.kuro.expensetracker.models.Budget;
import com.kuro.expensetracker.models.PaymentMode;
import com.kuro.expensetracker.models.Transaction;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

@Data
public class UserRequest {
    private Long id;
    private String name;
    @Email(message = "Must be a well-formed email address.")
    @NotNull(message = "Must provide an email.")
    private String email;
    @NotNull(message = "Must provide a password.")
    private String password;
    private LocalDate joinedAt;
    private Currency currency;
    private List<Transaction> transactions;
    private List<Budget> budgets;
    private List<PaymentMode> paymentModes;
}
