package com.kuro.expensetracker.requests;

import com.kuro.expensetracker.models.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class TransactionRequest {
    private Long id;
    @NotNull(message = "Must provide a title")
    private String title;
    private String description;
    private LocalDateTime transactionDate;
    @NotNull(message = "Must provide an amount")
    private BigDecimal amount;
    private String category;
    private User owner;

    public boolean isEmpty() {
        return (title == null || title.isBlank()) &&
                (description == null || description.isBlank()) &&
                amount == null &&
                category == null;
    }
}
