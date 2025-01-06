package com.kuro.expensetracker.services.transaction.subscription;

import com.kuro.expensetracker.enums.Frequency;
import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.models.Subscription;
import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.repositories.SubscriptionRepository;
import com.kuro.expensetracker.requests.SubscriptionRequest;
import com.kuro.expensetracker.requests.TransactionRequest;
import com.kuro.expensetracker.services.category.CategoryService;
import com.kuro.expensetracker.services.transaction.TransactionService;
import com.kuro.expensetracker.utils.DateTimeUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class SubscriptionService extends TransactionService<Subscription> implements ISubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(CategoryService categoryService, SubscriptionRepository subscriptionRepository) {
        super(subscriptionRepository, categoryService);
        this.subscriptionRepository = subscriptionRepository;
        setType(Subscription.class);
    }

    @Transactional
    @Override
    public <R extends TransactionRequest> Subscription create(R request) throws InvalidValueException {
        var subscription = createSubscriptionFromRequest((SubscriptionRequest) request, super.create(request));
        return subscriptionRepository.save(subscription);
    }


    @Override
    public Subscription createFromRequest(TransactionRequest request, Category category) {
        return new Subscription(
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
    public <R extends TransactionRequest> Subscription update(R request, Long incomeId) throws EntityNotFoundException {
        var subscription = super.update((TransactionRequest) request, incomeId);
        createSubscriptionFromRequest((SubscriptionRequest) request, subscription);
        return subscriptionRepository.save(subscription);
    }

    private Subscription createSubscriptionFromRequest(SubscriptionRequest request, Subscription subscription) {
        try {
            if (request.getFrequency() == null || request.getFrequency().isBlank()) {
                subscription.setFrequency(Frequency.ONCE);
            } else {
                subscription.setFrequency(Frequency.valueOf(request.getFrequency()));
            }
            subscription.setDueDate(request.getDueDate());
            subscription.setIsActive();
            return subscription;
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException("Frequency must be one of these values " + Arrays.toString(Frequency.values()));
        }
    }


    @Override
    public BigDecimal getTotal() {
        return subscriptionRepository.findTotalSubscriptionsByOwnerId(ownerId).orElse(new BigDecimal(0));
    }

    @Override
    public BigDecimal getTotalBetween(LocalDate startDate, LocalDate endDate) {
        return subscriptionRepository.findTotalSubscriptionsByOwnerIdBetween(ownerId,
                DateTimeUtil.getStartOfDay(startDate),
                DateTimeUtil.getEndOfDay(endDate)).orElse(new BigDecimal(0));
    }

    @Override
    public List<Subscription> getByCategoryAndDueDateBefore(String categoryName, LocalDate dueDateBefore) {
        if (categoryName != null && !categoryName.isBlank()) {
            return subscriptionRepository.findByOwnerIdAndCategoryNameAndDueDateBefore(
                    ownerId,
                    categoryName,
                    dueDateBefore);
        }
        return subscriptionRepository.findByOwnerIdAndDueDateBefore(
                ownerId,
                dueDateBefore);
    }

    @Override
    public List<Subscription> getByCategoryAndDueDateAfter(String categoryName, LocalDate dueDateAfter) {
        if (categoryName != null && !categoryName.isBlank()) {
            return subscriptionRepository.findByOwnerIdAndCategoryNameAndDueDateAfter(
                    ownerId,
                    categoryName,
                    dueDateAfter);
        }
        return subscriptionRepository.findByOwnerIdAndDueDateAfter(
                ownerId,
                dueDateAfter);
    }

    @Override
    public List<Subscription> getByActive(Boolean isActive) {
        return subscriptionRepository.findByOwnerIdAndIsActive(ownerId, isActive);
    }

    @Override
    public List<Subscription> getByCategoryAndDueDate(String categoryName, LocalDate dueDate) {
        if (categoryName != null && !categoryName.isBlank()) {
            return subscriptionRepository.findByOwnerIdAndCategoryNameAndDueDate(
                    ownerId,
                    categoryName,
                    dueDate);
        }
        return subscriptionRepository.findByOwnerIdAndDueDate(
                ownerId,
                dueDate);
    }

    @Override
    public List<Long> updateOverdueSubscriptions() {
        // Get all subscriptions that have passed their due date
        List<Subscription> subscriptions = subscriptionRepository.findOverdueSubscriptions();

        subscriptions.forEach(subscription -> subscription.setIsActive(false));
        subscriptionRepository.saveAll(subscriptions);

        return subscriptions.stream().map(Transaction::getId).toList();
    }
}
