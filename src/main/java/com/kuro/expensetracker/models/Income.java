package com.kuro.expensetracker.models;

import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class Income extends Transaction {
    public Income(String title, String description, Float amount, Category category, LocalDate transactionDate, User user) {
        super(title, description, amount, category, transactionDate, user);
    }
}
