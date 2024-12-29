package com.kuro.expensetracker.services.transaction.expense;

import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Expense;
import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.repositories.CategoryRepository;
import com.kuro.expensetracker.repositories.ExpenseRepository;
import com.kuro.expensetracker.repositories.TransactionRepository;
import com.kuro.expensetracker.requests.ExpenseRequest;
import com.kuro.expensetracker.requests.TransactionRequest;
import com.kuro.expensetracker.services.transaction.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ExpenseService extends TransactionService implements IExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(TransactionRepository transactionRepository, CategoryRepository categoryRepository, ExpenseRepository expenseRepository) {
        super(transactionRepository, categoryRepository);
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    @Override
    public Expense create(TransactionRequest request) throws InvalidValueException {
        var transaction = super.create(request);
        var income = getExpenseFromTransaction(transaction);
        return expenseRepository.save(income);
    }

    @Override
    public Expense update(ExpenseRequest request, Long expenseId) throws EntityNotFoundException {
        return expenseRepository.findByIdAndOwnerId(expenseId, request.getOwner().getId())
                .map(existingTransaction -> getExpenseFromTransaction(super.update(existingTransaction, request)))
                .map(transactionRepository::save)
                .orElseThrow(() -> new EntityNotFoundException(Expense.class, expenseId));
    }

    private Expense getExpenseFromTransaction(Transaction transaction) {
        return new Expense(
                transaction.getId(),
                transaction.getTitle(),
                transaction.getDescription(),
                transaction.getAmount().signum() > 0 ? transaction.getAmount().negate() : transaction.getAmount(),
                transaction.getCategory(),
                transaction.getTransactionDate(),
                transaction.getOwner()
        );
    }

    @Override
    public Expense getById(Long id) throws EntityNotFoundException {
        return expenseRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new EntityNotFoundException(Expense.class, id));
    }

    @Override
    public List<Expense> getAll() {
        return expenseRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Expense> getByCategory(Long categoryId) {
        return expenseRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Expense> getByCategory(String categoryName) {
        return expenseRepository.findByCategoryName(categoryName);
    }

    @Override
    public List<Expense> getByTransactionDate(LocalDate date) {
        return expenseRepository.findByTransactionDate(date);
    }

    @Override
    public List<Expense> getBeforeDate(LocalDate date) {
        return expenseRepository.findByTransactionDateBefore(date);
    }

    @Override
    public List<Expense> getAfterDate(LocalDate date) {
        return expenseRepository.findByTransactionDateAfter(date);
    }

    @Override
    public List<Expense> getBetweenDate(LocalDate minDate, LocalDate maxDate) {
        return expenseRepository.findByTransactionDateBetween(minDate, maxDate);
    }

    @Override
    public BigDecimal getTotal() {
        return expenseRepository.findTotalExpensesByOwnerId(ownerId).orElse(new BigDecimal(0));
    }

    @Override
    public BigDecimal getTotalBetween(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findTotalExpensesByOwnerIdBetween(ownerId,
                LocalDateTime.of(startDate, LocalTime.of(23, 59, 59)),
                LocalDateTime.of(endDate, LocalTime.of(23, 59, 59))).orElse(new BigDecimal(0));
    }
}
