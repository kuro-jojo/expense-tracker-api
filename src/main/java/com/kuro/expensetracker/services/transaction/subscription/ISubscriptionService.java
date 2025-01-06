package com.kuro.expensetracker.services.transaction.subscription;

import com.kuro.expensetracker.enums.Frequency;
import com.kuro.expensetracker.models.Subscription;

import java.time.LocalDate;
import java.util.List;

public interface ISubscriptionService {
    List<Subscription> getByCategoryAndDueDateBefore(String categoryName, LocalDate dueDateBefore);

    List<Subscription> getByCategoryAndDueDateAfter(String categoryName, LocalDate dueDateAfter);

    List<Subscription> getByActive(Boolean isActive);

    List<Subscription> getByCategoryAndDueDate(String categoryName, LocalDate dueDate);

    List<Long> updateOverdueSubscriptions();

    List<Subscription> getByFrequency(Frequency frequency);
}
