package com.kuro.expensetracker.requests;

import com.kuro.expensetracker.enums.Frequency;
import com.kuro.expensetracker.requests.TransactionRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@EqualsAndHashCode(callSuper = true)
@Data
public class SubscriptionRequest extends TransactionRequest {
    private LocalDate dueDate;
    private String frequencyString;
    private Frequency frequency;
    private Boolean isActive;
}
