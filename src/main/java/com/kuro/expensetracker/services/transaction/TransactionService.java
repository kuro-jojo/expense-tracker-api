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
import com.kuro.expensetracker.utils.DateTimeUtil;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public <R extends TransactionRequest> T create(R request) throws InvalidValueException {
        if (request.getAmount() == null) {
            throw new InvalidValueException("Amount cannot be empty!");
        }
        if ((request instanceof IncomeRequest && request.getAmount().signum() < 0) ||
                (request instanceof ExpenseRequest && request.getAmount().signum() > 0)) {
            request.setAmount(request.getAmount().negate());
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
    public <R extends TransactionRequest> T update(R request, Long transactionId) throws EntityNotFoundException {
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
    public List<T> getAll(Pageable pageable) {
        return transactionRepository.findByOwnerId(ownerId, pageable);
    }


    @Override
    public List<T> getByCategory(String categoryName, Pageable pageable) {
        if (categoryName == null || categoryName.isBlank()) {
            return getAll(pageable);
        }
        return transactionRepository.findByOwnerIdAndCategoryName(ownerId, categoryName, pageable);
    }

    @Override
    public List<T> getByCategoryAndDateBetween(@Nullable String categoryName, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (categoryName == null || categoryName.isBlank()) {
            return transactionRepository.findByOwnerIdAndTransactionDateBetween(
                    ownerId,
                    DateTimeUtil.getStartOfDay(startDate),
                    DateTimeUtil.getEndOfDay(endDate),
                    pageable);
        }
        return transactionRepository.findByOwnerIdAndCategoryNameAndTransactionDateBetween(
                ownerId,
                categoryName,
                DateTimeUtil.getStartOfDay(startDate),
                DateTimeUtil.getEndOfDay(endDate),
                pageable);
    }

    @Override
    public List<T> getByCategoryAndDateBefore(@Nullable String categoryName, LocalDate beforeDate, Pageable pageable) {
        if (categoryName == null || categoryName.isBlank()) {
            return transactionRepository.findByOwnerIdAndTransactionDateBefore(
                    ownerId,
                    DateTimeUtil.getEndOfDay(beforeDate),
                    pageable);
        }
        return transactionRepository.findByOwnerIdAndCategoryNameAndTransactionDateBefore(
                ownerId,
                categoryName,
                DateTimeUtil.getEndOfDay(beforeDate),
                pageable);
    }

    @Override
    public List<T> getByCategoryAndDateAfter(@Nullable String categoryName, LocalDate afterDate, Pageable pageable) {
        if (categoryName == null || categoryName.isBlank()) {
            return transactionRepository.findByOwnerIdAndTransactionDateAfter(
                    ownerId,
                    DateTimeUtil.getStartOfDay(afterDate),
                    pageable);
        }
        return transactionRepository.findByOwnerIdAndCategoryNameAndTransactionDateAfter(
                ownerId,
                categoryName,
                DateTimeUtil.getStartOfDay(afterDate),
                pageable);
    }

    @Override
    public List<T> getByCategoryAndDateWeek(String categoryName, Pageable pageable) {
        if (categoryName == null || categoryName.isBlank()) {
            return transactionRepository.findByOwnerIdAndTransactionDateBetween(
                    ownerId,
                    DateTimeUtil.getStartOfWeek(),
                    DateTimeUtil.getEndOfWeek(),
                    pageable);
        }
        return transactionRepository.findByOwnerIdAndCategoryNameAndTransactionDateBetween(
                ownerId,
                categoryName,
                DateTimeUtil.getStartOfWeek(),
                DateTimeUtil.getEndOfWeek(),
                pageable);
    }

    @Override
    public List<T> getByCategoryAndDateYear(String categoryName, Pageable pageable) {
        if (categoryName == null || categoryName.isBlank()) {
            return transactionRepository.findByOwnerIdAndTransactionDateBetween(
                    ownerId,
                    DateTimeUtil.getFirstDayOfYear(),
                    DateTimeUtil.getLastDayOfYear(),
                    pageable);
        }
        return transactionRepository.findByOwnerIdAndCategoryNameAndTransactionDateBetween(
                ownerId,
                categoryName,
                DateTimeUtil.getFirstDayOfYear(),
                DateTimeUtil.getLastDayOfYear(),
                pageable);
    }

    @Override
    public List<T> getByCategoryAndDateToday(String categoryName, Pageable pageable) {
        if (categoryName == null || categoryName.isBlank()) {
            return transactionRepository.findByOwnerIdAndTransactionDateAfter(
                    ownerId,
                    DateTimeUtil.getStartOfToday(),
                    pageable);
        }
        return transactionRepository.findByOwnerIdAndCategoryNameAndTransactionDateAfter(
                ownerId,
                categoryName,
                DateTimeUtil.getEndOfToday(),
                pageable);
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
