package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    Optional<Income> findByIdAndOwnerId(Long incomeId, Long userId);

    List<Income> findByOwnerId(Long userId);

    List<Income> findByCategoryId(Long categoryId);

    List<Income> findByCategoryName(String categoryName);

    List<Income> findByTransactionDateBefore(LocalDate date);

    List<Income> findByTransactionDateAfter(LocalDate date);

    List<Income> findByTransactionDateBetween(LocalDate minDate, LocalDate maxDate);

    List<Income> findByTransactionDate(LocalDate date);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.owner.id = :ownerId")
    Optional<BigDecimal> findTotalIncomesByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.owner.id = :ownerId AND i.transactionDate BETWEEN :s AND :e")
    Optional<BigDecimal> findTotalIncomesByOwnerIdBetween(@Param("ownerId") Long ownerId, @Param("s") LocalDateTime startDate, @Param("e") LocalDateTime endDate);
}