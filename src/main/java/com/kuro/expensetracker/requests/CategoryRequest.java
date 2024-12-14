package com.kuro.expensetracker.requests;

import com.kuro.expensetracker.models.Transaction;
import lombok.Data;

import java.util.List;

@Data
public class CategoryRequest {
    private Long id;
    private String name;
    private String description;
    private Float threshold;
    private List<Transaction> transactions;

}
