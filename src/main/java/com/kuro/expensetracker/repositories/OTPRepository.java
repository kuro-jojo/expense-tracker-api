package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {

    Optional<OTP> findBySessionIDAndOtp(String sessionID, String otp);

    Optional<OTP> findBySessionID(String sessionID);

    Optional<OTP> findByEmail(String email);

    @Query("SELECT o FROM OTP o WHERE o.expiration <= NOW()")
    List<OTP> findExpiredOTPs();
}
