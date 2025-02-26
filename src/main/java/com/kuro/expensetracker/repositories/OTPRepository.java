package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {

    Optional<OTP> findByUserIdAndOtp(Long userId, String otp);

    @Query("SELECT o FROM OTP o WHERE o.otp = :otp AND o.user.email = :email")
    Optional<OTP> findByEmailAndOtp(@Param("email") String email, @Param("otp") String otp);

    @Query("SELECT o FROM OTP o WHERE o.expiration <= NOW()")
    List<OTP> findExpiredOTPs();
}
