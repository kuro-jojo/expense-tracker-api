package com.kuro.expensetracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
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
    @NotNull
    protected Float amount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("transactions")
    protected Category category;

    @JsonIgnore
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

    public Transaction(Long id, String title, String description, Float amount, Category category, LocalDate transactionDate, User user) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.transactionDate = transactionDate;
        this.user = user;
    }

}
