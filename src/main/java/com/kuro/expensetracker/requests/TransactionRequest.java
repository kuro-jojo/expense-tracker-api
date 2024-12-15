package com.kuro.expensetracker.requests;

import com.kuro.expensetracker.models.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;


@Data
public class TransactionRequest {
    private Long id;
    private String title;
    private String description;
    private LocalDate transactionDate;
    @NotNull
    private Float amount;
    private String category;
    private User user;


}
