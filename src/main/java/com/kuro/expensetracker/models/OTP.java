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
    @Column(nullable = false)
    private String otp;
    private LocalDateTime expiration;
    @Column(nullable = false, unique = true)
    private String sessionID;
    @Column(nullable = false)
    private String email;

    public boolean isValid() {
        return expiration.isAfter(LocalDateTime.now());
    }
}
