package com.kuro.expensetracker.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Column(nullable = false)
    protected String title;
    protected String description;
    @Column(nullable = false)
    protected LocalDate transactionDate;
    @Column(nullable = false)
    protected Float amount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    protected Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    protected User user;

    public Transaction(String title, String description, Float amount, Category category, LocalDate transactionDate, User user) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.transactionDate = transactionDate;
        this.user = user;
    }

}
