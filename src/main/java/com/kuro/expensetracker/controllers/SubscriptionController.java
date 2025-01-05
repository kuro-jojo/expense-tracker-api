package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.models.Subscription;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.SubscriptionRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.transaction.subscription.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/subscriptions")
public class SubscriptionController extends TransactionController<Subscription> {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        super(subscriptionService);
        this.subscriptionService = subscriptionService;
        setType(Subscription.class);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> createSubscription(@RequestBody @Valid SubscriptionRequest request, @AuthenticationPrincipal User user) {
        return super.createTransaction(request, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getSubscriptionById(@PathVariable String id, @AuthenticationPrincipal User user) {
        return super.getTransactionById(id, user);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse> getSubscriptionsByField(
            @RequestParam(required = false, value = "c") String categoryName,
            @RequestParam(required = false, value = "b") String beforeDate,
            @RequestParam(required = false, value = "a") String afterDate,
            @RequestParam(required = false, value = "s") String startDate,
            @RequestParam(required = false, value = "e") String endDate,
            @RequestParam(required = false, value = "period") String period,
            @AuthenticationPrincipal User user, Pageable pageable) {
        return super.getTransactionByCategoryFilteredByDate(
                categoryName, beforeDate, afterDate, startDate, endDate, period, user, pageable);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSubscriptionById(
            @PathVariable String id,
            @RequestBody SubscriptionRequest request,
            @AuthenticationPrincipal User user
    ) {
        return super.updateTransactionById(id, request, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSubscriptionById(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        return super.deleteTransactionById(id, user);
    }

    @GetMapping("total")
    public ResponseEntity<ApiResponse> getTotalOfSubscriptions(
            @RequestParam(required = false, value = "s") String startDate,
            @RequestParam(required = false, value = "e") String endDate,
            @AuthenticationPrincipal User user
    ) {
        return super.getTotalOfTransactions(startDate, endDate, user);
    }
}
