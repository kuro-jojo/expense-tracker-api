package com.kuro.expensetracker.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@DiscriminatorValue("income")
public class Income extends Transaction {
    public Income(String title, String description, Float amount, Category category, LocalDate transactionDate, User user) {
        super(title, description, amount, category, transactionDate, user);
    }

    public Income(Long id, String title, String description, Float amount, Category category, LocalDate transactionDate, User user) {
        super(id, title, description, amount, category, transactionDate, user);
    }
}
