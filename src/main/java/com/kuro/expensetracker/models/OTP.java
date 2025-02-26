package com.kuro.expensetracker.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Length(min = 6, max = 6)
    private String otp;
    private LocalDateTime expiration;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public boolean isValid() {
        return expiration.isAfter(LocalDateTime.now());
    }
}
