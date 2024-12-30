package com.kuro.expensetracker.services.transaction;

import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.repositories.TransactionRepository;
import com.kuro.expensetracker.requests.CategoryRequest;
import com.kuro.expensetracker.requests.ExpenseRequest;
import com.kuro.expensetracker.requests.IncomeRequest;
import com.kuro.expensetracker.requests.TransactionRequest;
import com.kuro.expensetracker.services.category.CategoryService;
import jakarta.transaction.Transactional;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@Setter
public class TransactionService<T extends Transaction> implements ITransactionService<T> {
    protected final TransactionRepository<T> transactionRepository;
    protected final CategoryService categoryService;
    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final String DEFAULT_CATEGORY_NAME = "default";
    protected Long ownerId;
    private Class<T> type;

    public TransactionService(TransactionRepository<T> transactionRepository, CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
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

        categoryService.setOwnerId(request.getOwner().getId());

        Category category;
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            try {
                category = categoryService.getByName(request.getCategory());
            } catch (EntityNotFoundException e) {
                category = categoryService.create(CategoryRequest.builder()
                        .name(request.getCategory())
                        .owner(request.getOwner())
                        .build());
                logger.atInfo()
                        .addKeyValue("details",
                                Map.of(getClassType() + "_id", category.getId(),
                                        getClassType() + "_name", category.getName()))
                        .log("[UUID={}] Category not found. Creating a new category", request.getOwner().getUuid());

            }
        } else {
            try {
                category = categoryService.getByName(DEFAULT_CATEGORY_NAME);
            } catch (EntityNotFoundException e) {
                category = categoryService.create(CategoryRequest.builder()
                        .name(DEFAULT_CATEGORY_NAME)
                        .owner(request.getOwner())
                        .build());
                logger.atInfo()
                        .addKeyValue("details",
                                Map.of(getClassType() + "_id", category.getId(),
                                        getClassType() + "_name", category.getName()))
                        .log("[UUID={}] No category provided. Creating a default category", request.getOwner().getUuid());
            }
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
                    categoryService.setOwnerId(request.getOwner().getId());

                    if (request.getCategory() != null) {
                        var category = categoryService.getByName(request.getCategory());
                        existingTransaction.setCategory(category);
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

    private String getClassType() {
        return type.getSimpleName().toLowerCase();
    }
}
