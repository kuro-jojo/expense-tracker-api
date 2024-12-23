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
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
        if (request instanceof IncomeRequest && request.getAmount() < 0) {
            throw new InvalidValueException("Amount cannot be a negative value!");
        }
        if (request instanceof ExpenseRequest && request.getAmount() > 0) {
            throw new InvalidValueException("Amount cannot be a positive value!");
        }

        Category category = null;
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            category = categoryRepository.findByNameAndOwnerId(request.getCategory(), request.getOwner().getId())
                    .orElseGet(() -> {
                        Category newCategory = new Category(request.getCategory(), request.getOwner());
                        return categoryRepository.save(newCategory);
                    });
        }
        if (request.getTransactionDate() == null) {
            request.setTransactionDate(LocalDate.now());
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
    public Transaction update(TransactionRequest request, Long transactionId) throws EntityNotFoundException {

        return transactionRepository.findByIdAndOwnerId(transactionId, request.getOwner().getId())
                .map(existingTransaction -> updateTransaction(existingTransaction, request))
                .map(transactionRepository::save)
                .orElseThrow(() -> new EntityNotFoundException(Transaction.class, transactionId));
    }

    private Transaction updateTransaction(Transaction existingTransaction, TransactionRequest request) throws EntityNotFoundException {
        existingTransaction.setTitle(request.getTitle());
        existingTransaction.setDescription(request.getDescription());
        existingTransaction.setTransactionDate(request.getTransactionDate());
        existingTransaction.setAmount(request.getAmount());

        categoryRepository.findByNameAndOwnerId(request.getCategory(), request.getOwner().getId()).ifPresentOrElse(
                existingTransaction::setCategory,
                () -> {
                    throw new EntityNotFoundException(Category.class, request.getCategory());
                }
        );
        return existingTransaction;
    }

    @Override
    public void deleteById(Long id) throws EntityNotFoundException {
        transactionRepository.findByIdAndOwnerId(id, ownerId)
                .ifPresentOrElse(transactionRepository::delete,
                        () -> {
                            throw new EntityNotFoundException(Transaction.class, id);
                        });
    }

    @Override
    public Transaction getById(Long id) throws EntityNotFoundException {
        return transactionRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));
    }

    @Override
    public List<Transaction> getAll() {
        return transactionRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Transaction> getByCategory(Long categoryId) {
        return transactionRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Transaction> getByCategory(String categoryName) {
        return transactionRepository.findByCategoryName(categoryName);
    }

    @Override
    public List<Transaction> getByTransactionDate(LocalDate date) {
        return transactionRepository.findByTransactionDate(date);
    }

    @Override
    public List<Transaction> getBefore(LocalDate date) {
        return transactionRepository.findByTransactionDateBefore(date);
    }

    @Override
    public List<Transaction> getAfter(LocalDate date) {
        return transactionRepository.findByTransactionDateAfter(date);
    }

    @Override
    public List<Transaction> getBetween(LocalDate minDate, LocalDate maxDate) {
        return transactionRepository.findByTransactionDateBetween(minDate, maxDate);
    }
}
