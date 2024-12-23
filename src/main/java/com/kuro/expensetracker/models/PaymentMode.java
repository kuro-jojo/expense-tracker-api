package com.kuro.expensetracker.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PaymentMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    // TODO: complete this with the relevant fields

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
}
