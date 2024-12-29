package com.kuro.expensetracker.models;

import com.kuro.expensetracker.enums.Frequency;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@DiscriminatorValue("subs")
public class Subscription extends Transaction {
    private LocalDate dueDate;
    private Frequency frequency;
    private Boolean isActive;

    public Subscription(String title, String description, BigDecimal amount, Category category, LocalDateTime transactionDate, User user, LocalDate dueDate, Frequency frequency, Boolean isActive) {
        super(title, description, amount, category, transactionDate, user);
        this.dueDate = dueDate;
        this.frequency = frequency;
        this.isActive = isActive;
    }
}
