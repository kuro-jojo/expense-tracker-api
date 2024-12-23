package com.kuro.expensetracker.requests;

import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.models.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CategoryRequest {
    private Long id;
    @NotNull
    private String name;
    private String description;
    private Float threshold;
    private List<Transaction> transactions;
    private User owner;
}
