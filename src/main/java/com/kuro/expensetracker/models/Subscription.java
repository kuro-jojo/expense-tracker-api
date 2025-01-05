package com.kuro.expensetracker.models;

import com.kuro.expensetracker.enums.Frequency;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.YesNoConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Setter
@Getter
@DiscriminatorValue("subs")
public class Subscription extends Transaction {
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private Frequency frequency;
    @Convert(converter = YesNoConverter.class)
    private Boolean isActive;

    public Subscription(Long id, String title, String description, BigDecimal amount, Category category, LocalDateTime transactionDate, User owner) {
        super(id, title, description, amount, category, transactionDate, owner);
    }

    public void setDueDate(LocalDate dueDate) {
        if (dueDate != null) {
            this.dueDate = dueDate;
        } else {
            switch (frequency) {
                case ONCE -> this.dueDate = LocalDate.now().plusDays(1);
                case WEEKLY -> this.dueDate = LocalDate.now().plusWeeks(1);
                case MONTHLY -> this.dueDate = LocalDate.now().plusMonths(1);
                case YEARLY -> this.dueDate = LocalDate.now().plusYears(1);
            }
        }
    }

    public void setIsActive() {
        this.isActive = dueDate.isAfter(LocalDate.now());
    }
}
