package com.kuro.expensetracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transaction_type")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Column(nullable = false)
    protected String title;
    protected String description;
    @Column(nullable = false)
    protected LocalDateTime transactionDate;
    @Column(nullable = false)
    @NotNull
    protected BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("transactions")
    protected Category category;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    protected User owner;

    public Transaction(String title, String description, BigDecimal amount, Category category, LocalDateTime transactionDate, User owner) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.transactionDate = transactionDate;
        this.owner = owner;
    }

    public Transaction(Long id, String title, String description, BigDecimal amount, Category category, LocalDateTime transactionDate, User owner) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.transactionDate = transactionDate;
        this.owner = owner;
    }

}
