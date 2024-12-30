package com.kuro.expensetracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "owner"}))
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    private BigDecimal threshold;

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

    public Category(String name, String description, BigDecimal threshold, List<Transaction> transactions, User owner) {
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
