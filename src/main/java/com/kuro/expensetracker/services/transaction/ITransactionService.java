package com.kuro.expensetracker.services.transaction;

import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.requests.TransactionRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ITransactionService<T extends Transaction> {
    T create(TransactionRequest request);

    T createFromRequest(TransactionRequest request, Category category);

    T update(TransactionRequest request, Long transactionId) throws EntityNotFoundException;

    void deleteById(Long id) throws EntityNotFoundException;

    T getById(Long id) throws EntityNotFoundException;

    List<T> getAll(Pageable pageable);

    List<T> getByCategory(String categoryName);

    List<T> getByCategoryAndDateBetween(String categoryName, LocalDate startDate, LocalDate endDate);

    List<T> getByCategoryAndDateBefore(String categoryName, LocalDate beforeDate);

    List<T> getByCategoryAndDateAfter(String categoryName, LocalDate afterDate);

    List<T> getByDateBetween(LocalDate startDate, LocalDate endDate);

    List<T> getByDateBefore(LocalDate dateBefore);

    List<T> getByDateAfter(LocalDate afterDate);

    BigDecimal getTotal();

    BigDecimal getTotalBetween(LocalDate startDate, LocalDate endDate);
}
