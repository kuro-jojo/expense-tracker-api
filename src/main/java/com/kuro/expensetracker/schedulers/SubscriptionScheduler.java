package com.kuro.expensetracker.schedulers;

import com.kuro.expensetracker.services.transaction.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {
    private final SubscriptionService subscriptionService;

    private final Logger logger = LoggerFactory.getLogger(SubscriptionScheduler.class);

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    public void checkOverdueSubscriptions() {
        logger.info("Updating the subscriptions status.");
        var ids = subscriptionService.updateOverdueSubscriptions();
        if (ids.isEmpty()) {
            logger.info("No subscription updated.");
        } else {
            ids.forEach(id -> logger.info("Updated subscription with id #{}.", id));
        }
    }
}