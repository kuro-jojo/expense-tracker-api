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

    List<Expense> getByCategory(String categoryName);

    BigDecimal getTotal();

    BigDecimal getTotalBetween(LocalDate startDate, LocalDate endDate);

    List<Expense> getByCategoryAndDateBetween(String categoryName, LocalDate startDate, LocalDate endDate);

    List<Expense> getByCategoryAndDateBefore(String categoryName, LocalDate beforeDate);

    List<Expense> getByCategoryAndDateAfter(String categoryName, LocalDate afterDate);

    List<Expense> getByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Expense> getByDateBefore(LocalDate dateBefore);

    List<Expense> getByDateAfter(LocalDate afterDate);
}
