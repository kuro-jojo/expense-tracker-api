package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.ConfirmationEmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConfirmationEmailTokenRepository extends JpaRepository<ConfirmationEmailToken, Long> {
    Optional<ConfirmationEmailToken> findByToken(String token);

    @Query("SELECT t FROM ConfirmationEmailToken t WHERE t.expiration <= NOW()")
    List<ConfirmationEmailToken> findExpiredTokens();
}
