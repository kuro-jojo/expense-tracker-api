package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByCategoryId(Long categoryId);

    List<Transaction> findByCategoryName(String categoryName);

    List<Transaction> findByTransactionDateBefore(LocalDate date);

    List<Transaction> findByTransactionDateAfter(LocalDate date);

    List<Transaction> findByTransactionDateBetween(LocalDate minDate, LocalDate maxDate);
}
