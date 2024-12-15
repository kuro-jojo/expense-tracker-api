package com.kuro.expensetracker.models;

import com.kuro.expensetracker.enums.Frequency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class Subscription extends Transaction {
    private LocalDate dueDate;
    private Frequency frequency;
    private Boolean isActive;

    public Subscription(String title, String description, Float amount, Category category, LocalDate transactionDate, User user, LocalDate dueDate, Frequency frequency, Boolean isActive) {
        super(title, description, amount, category, transactionDate, user);
        this.dueDate = dueDate;
        this.frequency = frequency;
        this.isActive = isActive;
    }
}
