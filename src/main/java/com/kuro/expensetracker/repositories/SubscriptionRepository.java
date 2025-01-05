package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.Subscription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface SubscriptionRepository extends TransactionRepository<Subscription> {
    @Query("SELECT SUM(s.amount) FROM Subscription s WHERE s.owner.id = :ownerId")
    Optional<BigDecimal> findTotalSubscriptionsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT SUM(s.amount) FROM Subscription s WHERE s.owner.id = :ownerId AND s.transactionDate BETWEEN :start AND :e")
    Optional<BigDecimal> findTotalSubscriptionsByOwnerIdBetween(@Param("ownerId") Long ownerId, @Param("start") LocalDateTime startDate, @Param("e") LocalDateTime endDate);
}