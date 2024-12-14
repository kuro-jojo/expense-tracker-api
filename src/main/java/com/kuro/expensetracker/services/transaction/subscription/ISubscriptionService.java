package com.kuro.expensetracker.services.transaction.subscription;

import com.kuro.expensetracker.models.Subscription;
import com.kuro.expensetracker.requests.SubscriptionRequest;
import com.kuro.expensetracker.services.transaction.ITransactionService;

public interface ISubscriptionService extends ITransactionService {
     Subscription add(SubscriptionRequest request) ;
}
