package com.kuro.expensetracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    private Float threshold;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("category")
    private List<Transaction> transactions;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, String description, Float threshold, List<Transaction> transactions, User owner) {
        this.name = name;
        this.description = description;
        this.threshold = threshold;
        this.transactions = transactions;
        this.owner = owner;
    }

    public Category(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }
}
