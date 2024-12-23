package com.kuro.expensetracker.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate addedAt;
    private Float totalAmount;
    private Float amountSpent;
    private LocalDate startOfPeriod;
    private LocalDate endOfPeriod;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

}
