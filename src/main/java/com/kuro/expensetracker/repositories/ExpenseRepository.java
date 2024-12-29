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

    List<Expense> findByOwnerIdAndCategoryId(Long ownerId, Long categoryId);

    List<Expense> findByOwnerIdAndCategoryName(Long ownerId, String categoryName);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.owner.id = :ownerId")
    Optional<BigDecimal> findTotalExpensesByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.owner.id = :ownerId AND e.transactionDate BETWEEN :s AND :e")
    Optional<BigDecimal> findTotalExpensesByOwnerIdBetween(@Param("ownerId") Long ownerId, @Param("s") LocalDateTime startDate, @Param("e") LocalDateTime endDate);

    List<Expense> findByOwnerIdAndCategoryNameAndTransactionDateBetween(Long ownerId, String categoryName, LocalDateTime of, LocalDateTime of1);

    List<Expense> findByOwnerIdAndCategoryNameAndTransactionDateBefore(Long ownerId, String categoryName, LocalDateTime of);

    List<Expense> findByOwnerIdAndCategoryNameAndTransactionDateAfter(Long ownerId, String categoryName, LocalDateTime of);

    List<Expense> findByOwnerIdAndTransactionDateBetween(Long ownerId, LocalDateTime of, LocalDateTime of1);

    List<Expense> findByOwnerIdAndTransactionDateBefore(Long ownerId, LocalDateTime of);

    List<Expense> findByOwnerIdAndTransactionDateAfter(Long ownerId, LocalDateTime of);
}
