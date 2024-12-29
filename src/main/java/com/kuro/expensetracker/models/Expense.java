package com.kuro.expensetracker.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@DiscriminatorValue("expense")
public class Expense extends Transaction {

    public Expense(Long id, String title, String description, BigDecimal amount, Category category, LocalDateTime transactionDate, User owner) {
        super(id, title, description, amount, category, transactionDate, owner);
    }
}
