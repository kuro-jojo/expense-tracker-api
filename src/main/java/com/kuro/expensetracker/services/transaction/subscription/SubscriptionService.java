package com.kuro.expensetracker.services.transaction.subscription;

import com.kuro.expensetracker.enums.Frequency;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.models.Subscription;
import com.kuro.expensetracker.repositories.CategoryRepository;
import com.kuro.expensetracker.repositories.SubscriptionRepository;
import com.kuro.expensetracker.repositories.TransactionRepository;
import com.kuro.expensetracker.requests.SubscriptionRequest;
import com.kuro.expensetracker.services.transaction.TransactionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

@Service
public class SubscriptionService extends TransactionService implements ISubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository, SubscriptionRepository subscriptionRepository) {
        super(transactionRepository, categoryRepository);
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Subscription add(SubscriptionRequest request) {
        Category category = super.categoryRepository.findByName(request.getCategory().getName())
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        // check the frequency
        if (Arrays.stream(Frequency.values()).anyMatch(frequency -> frequency.name().equals(request.getFrequencyString()))) {
            request.setFrequency(Frequency.valueOf(request.getFrequencyString()));
        } else {
            throw new EnumConstantNotPresentException(Frequency.class, request.getFrequencyString());
        }

        // check the due date
        if (request.getDueDate().isBefore(LocalDate.now())) {
            request.setIsActive(false);
        }
        request.setCategory(category);
        subscriptionRepository.save(createTransaction(request));
        return null;
    }

    private Subscription createTransaction(SubscriptionRequest request) {
        return new Subscription(
                request.getTitle(),
                request.getDescription(),
                request.getAmount(),
                request.getCategory(),
                request.getTransactionDate(),
                request.getUser(),
                request.getDueDate(),
                request.getFrequency(),
                request.getIsActive()
        );
    }
}
