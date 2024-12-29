package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Optional<Expense> findByIdAndOwnerId(Long incomeId, Long ownerId);

    List<Expense> findByOwnerId(Long ownerId);

    List<Expense> findByCategoryId(Long categoryId);

    List<Expense> findByCategoryName(String categoryName);

    List<Expense> findByTransactionDateBefore(LocalDate date);

    List<Expense> findByTransactionDateAfter(LocalDate date);

    List<Expense> findByTransactionDateBetween(LocalDate minDate, LocalDate maxDate);

    List<Expense> findByTransactionDate(LocalDate date);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.owner = :ownerId")
    BigDecimal findTotalExpensesByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.owner = :ownerId AND e.transactionDate BETWEEN :s AND :e")
    BigDecimal findTotalExpensesByOwnerIdBetween(@Param("ownerId") Long ownerId, @Param("s") LocalDateTime startDate, @Param("e") LocalDateTime endDate);
}
