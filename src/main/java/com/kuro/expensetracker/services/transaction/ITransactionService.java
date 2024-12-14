package com.kuro.expensetracker.services.transaction;

import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.requests.TransactionRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ITransactionService {
    Transaction add(TransactionRequest request);
    Transaction update(TransactionRequest request, Long transactionId);

    void deleteById(Long id);
    Optional<Transaction> getById(Long id);
    List<Transaction> getByUser(Long userId);
    List<Transaction> getByCategory(Long categoryId);
    List<Transaction> getByCategory(String  categoryName);
    List<Transaction> getBefore(LocalDate date);
    List<Transaction> getAfter(LocalDate date);
    List<Transaction> getBetween(LocalDate minDate, LocalDate maxDate);

}
