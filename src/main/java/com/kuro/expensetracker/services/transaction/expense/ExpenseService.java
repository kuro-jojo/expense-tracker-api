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
    public List<Expense> getByCategory(String categoryName) {
        return expenseRepository.findByOwnerIdAndCategoryName(ownerId, categoryName);
    }

    @Override
    public BigDecimal getTotal() {
        return expenseRepository.findTotalExpensesByOwnerId(ownerId).orElse(new BigDecimal(0));
    }

    @Override
    public BigDecimal getTotalBetween(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findTotalExpensesByOwnerIdBetween(ownerId,
                LocalDateTime.of(startDate, LocalTime.of(0, 0, 0)),
                LocalDateTime.of(endDate, LocalTime.of(23, 59, 59))).orElse(new BigDecimal(0));
    }

    @Override
    public List<Expense> getByCategoryAndDateBetween(String categoryName, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByOwnerIdAndCategoryNameAndTransactionDateBetween(ownerId,
                categoryName,
                LocalDateTime.of(startDate, LocalTime.of(0, 0, 0)),
                LocalDateTime.of(endDate, LocalTime.of(23, 59, 59)));
    }

    @Override
    public List<Expense> getByCategoryAndDateBefore(String categoryName, LocalDate beforeDate) {
        return expenseRepository.findByOwnerIdAndCategoryNameAndTransactionDateBefore(ownerId,
                categoryName,
                LocalDateTime.of(beforeDate, LocalTime.of(23, 59, 59)));
    }

    @Override
    public List<Expense> getByCategoryAndDateAfter(String categoryName, LocalDate afterDate) {
        return expenseRepository.findByOwnerIdAndCategoryNameAndTransactionDateAfter(ownerId,
                categoryName,
                LocalDateTime.of(afterDate, LocalTime.of(0, 0, 0)));
    }

    @Override
    public List<Expense> getByDateBetween(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByOwnerIdAndTransactionDateBetween(ownerId,
                LocalDateTime.of(startDate, LocalTime.of(0, 0, 0)),
                LocalDateTime.of(endDate, LocalTime.of(23, 59, 59)));
    }

    @Override
    public List<Expense> getByDateBefore(LocalDate dateBefore) {
        return expenseRepository.findByOwnerIdAndTransactionDateBefore(ownerId,
                LocalDateTime.of(dateBefore, LocalTime.of(23, 59, 59)));
    }

    @Override
    public List<Expense> getByDateAfter(LocalDate afterDate) {
        return expenseRepository.findByOwnerIdAndTransactionDateAfter(ownerId,
                LocalDateTime.of(afterDate, LocalTime.of(0, 0, 0)));
    }
}
