package com.kuro.expensetracker.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    private String description;
    private Float threshold;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("category")
    private List<Transaction> transactions;

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, String description, Float threshold, List<Transaction> transactions) {
        this.name = name;
        this.description = description;
        this.threshold = threshold;
        this.transactions = transactions;
    }
}
