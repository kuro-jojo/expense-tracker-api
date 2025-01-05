package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository<T extends Transaction> extends JpaRepository<T, Long> {
    Optional<T> findByIdAndOwnerId(
            Long transactionId,
            Long ownerId);

    List<T> findByOwnerId(Long ownerId, Pageable pageable);

    List<T> findByOwnerIdAndCategoryName(Long ownerId, String categoryName);

    List<T> findByOwnerIdAndCategoryNameAndTransactionDateBetween(Long ownerId, String categoryName, LocalDateTime of, LocalDateTime of1);

    List<T> findByOwnerIdAndCategoryNameAndTransactionDateBefore(Long ownerId, String categoryName, LocalDateTime of);

    List<T> findByOwnerIdAndCategoryNameAndTransactionDateAfter(Long ownerId, String categoryName, LocalDateTime of);

    List<T> findByOwnerIdAndTransactionDateBetween(Long ownerId, LocalDateTime of, LocalDateTime of1);

    List<T> findByOwnerIdAndTransactionDateBefore(Long ownerId, LocalDateTime of);

    List<T> findByOwnerIdAndTransactionDateAfter(Long ownerId, LocalDateTime of);
}
