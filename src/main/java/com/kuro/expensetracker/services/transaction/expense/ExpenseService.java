package com.kuro.expensetracker.services.transaction.expense;

import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.models.Expense;
import com.kuro.expensetracker.repositories.ExpenseRepository;
import com.kuro.expensetracker.requests.TransactionRequest;
import com.kuro.expensetracker.services.category.CategoryService;
import com.kuro.expensetracker.services.transaction.TransactionService;
import com.kuro.expensetracker.utils.DateTimeUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ExpenseService extends TransactionService<Expense> {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(CategoryService categoryService, ExpenseRepository expenseRepository) {
        super(expenseRepository, categoryService);
        this.expenseRepository = expenseRepository;
        setType(Expense.class);
    }

    @Transactional
    @Override
    public Expense create(TransactionRequest request) throws InvalidValueException {
        var expense = super.create(request);
        return expenseRepository.save(expense);
    }

    @Override
    public Expense createFromRequest(TransactionRequest request, Category category) {
        return new Expense(
                request.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getAmount().signum() > 0 ? request.getAmount().negate() : request.getAmount(),
                category,
                request.getTransactionDate(),
                request.getOwner()
        );
    }

    @Override
    public Expense update(TransactionRequest request, Long expenseId) throws EntityNotFoundException {
        var expense = super.update(request, expenseId);
        return expenseRepository.save(expense);
    }

    @Override
    public BigDecimal getTotal() {
        return expenseRepository.findTotalExpensesByOwnerId(ownerId).orElse(new BigDecimal(0));
    }

    @Override
    public BigDecimal getTotalBetween(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findTotalExpensesByOwnerIdBetween(ownerId,
                DateTimeUtil.getStartOfDay(startDate),
                DateTimeUtil.getEndOfDay(endDate)).orElse(new BigDecimal(0));
    }
}