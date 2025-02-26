package com.kuro.expensetracker.requests;

import com.kuro.expensetracker.models.Budget;
import com.kuro.expensetracker.models.PaymentMode;
import com.kuro.expensetracker.models.Transaction;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

@Builder
@Data
public class UserRequest {
    private Long id;
    private String name;
    @NotNull(message = "Must provide an email.")
    private String email;
    @NotNull(message = "Must provide a password.")
    private String password;
    private LocalDate joinedAt;
    private Currency currency;
    private List<Transaction> transactions;
    private List<Budget> budgets;
    private List<PaymentMode> paymentModes;

    public boolean isEmpty() {
        return id == null &&
                (name == null || name.isBlank()) &&
                (email == null || email.isBlank()) &&
                (password == null || password.isBlank()) &&
                currency == null;
    }
}
