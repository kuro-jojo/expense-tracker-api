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

import java.time.LocalDateTime;

@Service
@Setter
public class TransactionService implements ITransactionService {
    protected final TransactionRepository transactionRepository;
    protected final CategoryRepository categoryRepository;
    protected Long ownerId;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Transaction create(TransactionRequest request) throws InvalidValueException {
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
        return createTransaction(request, category);
    }

    private Transaction createTransaction(TransactionRequest request, Category category) {
        return new Transaction(
                request.getTitle(),
                request.getDescription(),
                request.getAmount(),
                category,
                request.getTransactionDate(),
                request.getOwner()
        );
    }

    @Override
    public Transaction update(Transaction existingTransaction, TransactionRequest request) throws EntityNotFoundException {
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            existingTransaction.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            existingTransaction.setDescription(request.getDescription());
        }
        if (request.getAmount() != null) {
            existingTransaction.setAmount(request.getAmount());
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
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws EntityNotFoundException {
        transactionRepository.findByIdAndOwnerId(id, ownerId)
                .ifPresentOrElse(transactionRepository::delete,
                        () -> {
                            throw new EntityNotFoundException(Transaction.class, id);
                        });
    }
}
