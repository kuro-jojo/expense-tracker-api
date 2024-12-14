package com.kuro.expensetracker.requests;

import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.models.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Data
public class TransactionRequest {
    private Long id;
    private String title;
    private String description;
    private LocalDate transactionDate;
    private Float amount;
    private Category category;
    private User user;


}
