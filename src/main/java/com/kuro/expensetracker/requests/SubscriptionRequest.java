package com.kuro.expensetracker.requests;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@EqualsAndHashCode(callSuper = true)
@Data
public class SubscriptionRequest extends TransactionRequest {
    private LocalDate dueDate;
    private String frequency;
    private Boolean isActive;

    public void setFrequency(String frequency) {
        this.frequency = frequency.toUpperCase();
    }
}
