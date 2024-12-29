package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.Income;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface IncomeRepository extends TransactionRepository<Income> {
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.owner.id = :ownerId")
    Optional<BigDecimal> findTotalIncomesByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.owner.id = :ownerId AND i.transactionDate BETWEEN :s AND :e")
    Optional<BigDecimal> findTotalIncomesByOwnerIdBetween(@Param("ownerId") Long ownerId, @Param("s") LocalDateTime startDate, @Param("e") LocalDateTime endDate);
}