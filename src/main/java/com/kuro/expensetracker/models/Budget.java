package com.kuro.expensetracker.models;

import com.kuro.expensetracker.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
