package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.Expense;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ExpenseRepository extends TransactionRepository<Expense> {
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.owner.id = :ownerId")
    Optional<BigDecimal> findTotalExpensesByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.owner.id = :ownerId AND e.transactionDate BETWEEN :s AND :e")
    Optional<BigDecimal> findTotalExpensesByOwnerIdBetween(@Param("ownerId") Long ownerId, @Param("s") LocalDateTime startDate, @Param("e") LocalDateTime endDate);

}
