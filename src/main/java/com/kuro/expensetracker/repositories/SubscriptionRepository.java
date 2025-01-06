package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.enums.Frequency;
import com.kuro.expensetracker.models.Subscription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends TransactionRepository<Subscription> {
    @Query("SELECT SUM(s.amount) FROM Subscription s WHERE s.owner.id = :ownerId")
    Optional<BigDecimal> findTotalSubscriptionsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT SUM(s.amount) FROM Subscription s WHERE s.owner.id = :ownerId AND s.transactionDate BETWEEN :start AND :e")
    Optional<BigDecimal> findTotalSubscriptionsByOwnerIdBetween(@Param("ownerId") Long ownerId, @Param("start") LocalDateTime startDate, @Param("e") LocalDateTime endDate);

    List<Subscription> findByOwnerIdAndCategoryNameAndDueDateBefore(Long ownerId, String categoryName, LocalDate dueDateBefore);

    List<Subscription> findByOwnerIdAndDueDateBefore(Long ownerId, LocalDate dueDateBefore);

    List<Subscription> findByOwnerIdAndCategoryNameAndDueDateAfter(Long ownerId, String categoryName, LocalDate dueDateAfter);

    List<Subscription> findByOwnerIdAndDueDateAfter(Long ownerId, LocalDate dueDateAfter);

    List<Subscription> findByOwnerIdAndCategoryNameAndDueDate(Long ownerId, String categoryName, LocalDate dueDate);

    List<Subscription> findByOwnerIdAndDueDate(Long ownerId, LocalDate dueDate);

    List<Subscription> findByOwnerIdAndIsActive(Long ownerId, Boolean isActive);

    @Query("SELECT s FROM Subscription s WHERE s.isActive = true AND s.dueDate <= DATE(NOW())")
    List<Subscription> findOverdueSubscriptions();

    List<Subscription> findByOwnerIdAndFrequency(Long ownerId, Frequency frequency);
}