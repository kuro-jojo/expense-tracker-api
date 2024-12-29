package com.kuro.expensetracker.services.transaction.expense;

import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.models.Expense;
import com.kuro.expensetracker.requests.ExpenseRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IExpenseService {

    Expense update(ExpenseRequest request, Long expenseId) throws EntityNotFoundException;

    Expense getById(Long id) throws EntityNotFoundException;

    List<Expense> getAll();

    List<Expense> getByCategory(Long categoryId);

    List<Expense> getByCategory(String categoryName);

    List<Expense> getByTransactionDate(LocalDate date);

    List<Expense> getBeforeDate(LocalDate date);

    List<Expense> getAfterDate(LocalDate date);

    List<Expense> getBetweenDate(LocalDate minDate, LocalDate maxDate);

    BigDecimal getTotal();

    BigDecimal getTotalBetween(LocalDate startDate, LocalDate endDate);
}
