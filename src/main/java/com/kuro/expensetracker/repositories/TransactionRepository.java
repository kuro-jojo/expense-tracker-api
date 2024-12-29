package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdAndOwnerId(Long transactionId, Long userId);
}
