package com.kuro.expensetracker.services.transaction.income;

import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.models.Income;
import com.kuro.expensetracker.requests.IncomeRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IIncomeService {
    Income update(IncomeRequest request, Long incomeId) throws EntityNotFoundException;

    Income getById(Long id) throws EntityNotFoundException;

    List<Income> getAll();

    List<Income> getByCategory(Long categoryId);

    List<Income> getByCategory(String categoryName);

    List<Income> getByTransactionDate(LocalDate date);

    List<Income> getBeforeDate(LocalDate date);

    List<Income> getAfterDate(LocalDate date);

    List<Income> getBetweenDate(LocalDate minDate, LocalDate maxDate);

    BigDecimal getTotal();

    BigDecimal getTotalBetween(LocalDate startDate, LocalDate endDate);
}
