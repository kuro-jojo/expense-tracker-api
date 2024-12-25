package com.kuro.expensetracker.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EmailConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String token;
    private LocalDateTime expiration;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public boolean isValid() {
        return expiration.isAfter(LocalDateTime.now());
    }
}
