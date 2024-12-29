package com.kuro.expensetracker.services.transaction;

import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.repositories.CategoryRepository;
import com.kuro.expensetracker.repositories.TransactionRepository;
import com.kuro.expensetracker.requests.ExpenseRequest;
import com.kuro.expensetracker.requests.IncomeRequest;
import com.kuro.expensetracker.requests.TransactionRequest;
import jakarta.transaction.Transactional;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Setter
public class TransactionService<T extends Transaction> implements ITransactionService<T> {
    protected final TransactionRepository<T> transactionRepository;
    protected final CategoryRepository categoryRepository;
    protected Long ownerId;
    private Class<T> type;

    public TransactionService(TransactionRepository<T> transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public T create(TransactionRequest request) throws InvalidValueException {
        if (request.getAmount() == null) {
            throw new InvalidValueException("Amount cannot be empty!");
        }
        if (request instanceof IncomeRequest && request.getAmount().signum() < 0) {
            throw new InvalidValueException("Amount cannot be a negative value!");
        }
        if (request instanceof ExpenseRequest && request.getAmount().signum() > 0) {
            throw new InvalidValueException("Amount cannot be a positive value!");
        }

        Category category;
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            category = categoryRepository.findByNameAndOwnerId(request.getCategory(), request.getOwner().getId())
                    .orElseGet(() -> {
                        Category newCategory = new Category(request.getCategory(), request.getOwner());
                        return categoryRepository.save(newCategory);
                    });
        } else {
            category = new Category("default", request.getOwner());
            categoryRepository.save(category);
        }

        if (request.getTransactionDate() == null) {
            request.setTransactionDate(LocalDateTime.now());
        }

        return createFromRequest(request, category);
    }

    @Override
    public T createFromRequest(TransactionRequest request, Category category) {
        return null;
    }

    @Override
    public T update(TransactionRequest request, Long transactionId) throws EntityNotFoundException {
        return transactionRepository.findByIdAndOwnerId(transactionId, request.getOwner().getId())
                .map(existingTransaction -> {
                    if (request.getTitle() != null && !request.getTitle().isBlank()) {
                        existingTransaction.setTitle(request.getTitle());
                    }
                    if (request.getDescription() != null) {
                        existingTransaction.setDescription(request.getDescription());
                    }
                    if (request.getAmount() != null) {
                        if ((request instanceof IncomeRequest && request.getAmount().signum() < 0)
                                || (request instanceof ExpenseRequest && request.getAmount().signum() > 0)) {
                            existingTransaction.setAmount(request.getAmount().negate());
                        } else {
                            existingTransaction.setAmount(request.getAmount());
                        }
                    }
                    if (request.getCategory() != null) {
                        categoryRepository.findByNameAndOwnerId(request.getCategory(), request.getOwner().getId()).ifPresentOrElse(
                                existingTransaction::setCategory,
                                () -> {
                                    throw new EntityNotFoundException(Category.class, request.getCategory());
                                }
                        );
                    }
                    return existingTransaction;
                }).orElseThrow(() -> new EntityNotFoundException(type, transactionId));
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws EntityNotFoundException {
        transactionRepository.findByIdAndOwnerId(id, ownerId)
                .ifPresentOrElse(transactionRepository::delete,
                        () -> {
                            throw new EntityNotFoundException(type, id);
                        });
    }

    @Override
    public T getById(Long id) throws EntityNotFoundException {
        return transactionRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new EntityNotFoundException(type, id));
    }

    @Override
    public List<T> getAll() {
        return transactionRepository.findByOwnerId(ownerId);
    }


    @Override
    public List<T> getByCategory(String categoryName) {
        return transactionRepository.findByOwnerIdAndCategoryName(ownerId, categoryName);
    }

    @Override
    public List<T> getByCategoryAndDateBetween(String categoryName, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByOwnerIdAndCategoryNameAndTransactionDateBetween(
                ownerId,
                categoryName,
                LocalDateTime.of(startDate, LocalTime.of(0, 0, 0)),
                LocalDateTime.of(endDate, LocalTime.of(23, 59, 59)));
    }

    @Override
    public List<T> getByCategoryAndDateBefore(String categoryName, LocalDate beforeDate) {
        return transactionRepository.findByOwnerIdAndCategoryNameAndTransactionDateBefore(
                ownerId,
                categoryName,
                LocalDateTime.of(beforeDate, LocalTime.of(23, 59, 59)));
    }

    @Override
    public List<T> getByCategoryAndDateAfter(String categoryName, LocalDate afterDate) {
        return transactionRepository.findByOwnerIdAndCategoryNameAndTransactionDateAfter(
                ownerId,
                categoryName,
                LocalDateTime.of(afterDate, LocalTime.of(0, 0, 0)));
    }

    @Override
    public List<T> getByDateBetween(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByOwnerIdAndTransactionDateBetween(ownerId,
                LocalDateTime.of(startDate, LocalTime.of(0, 0, 0)),
                LocalDateTime.of(endDate, LocalTime.of(23, 59, 59)));
    }

    @Override
    public List<T> getByDateBefore(LocalDate dateBefore) {
        return transactionRepository.findByOwnerIdAndTransactionDateBefore(ownerId,
                LocalDateTime.of(dateBefore, LocalTime.of(23, 59, 59)));
    }

    @Override
    public List<T> getByDateAfter(LocalDate afterDate) {
        return transactionRepository.findByOwnerIdAndTransactionDateAfter(ownerId,
                LocalDateTime.of(afterDate, LocalTime.of(0, 0, 0)));
    }

    @Override
    public BigDecimal getTotal() {
        return null;
    }

    @Override
    public BigDecimal getTotalBetween(LocalDate startDate, LocalDate endDate) {
        return null;
    }
}
