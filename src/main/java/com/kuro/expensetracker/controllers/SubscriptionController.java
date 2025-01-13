package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.enums.Frequency;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Subscription;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.SubscriptionRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.export.TransactionExportService;
import com.kuro.expensetracker.services.transaction.subscription.SubscriptionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("${api.prefix}/subscriptions")
public class SubscriptionController extends TransactionController<Subscription> {
    private final SubscriptionService subscriptionService;
    private final Logger logger = LoggerFactory.getLogger(Subscription.class);

    public SubscriptionController(SubscriptionService subscriptionService, TransactionExportService<Subscription> transactionExportService) {
        super(subscriptionService, transactionExportService);
        this.subscriptionService = subscriptionService;

        transactionExportService.setTransactionType(Subscription.class);
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
            @RequestParam(required = false, value = "due") String dueDate,
            @RequestParam(required = false, value = "due-after") String dueDateAfter,
            @RequestParam(required = false, value = "due-before") String dueDateBefore,
            @RequestParam(required = false, value = "active") Boolean isActive,
            @RequestParam(required = false, value = "frequency") String frequency,
            @AuthenticationPrincipal User user, Pageable pageable) {

        if ((dueDate == null || dueDate.isBlank()) && (dueDateAfter == null || dueDateAfter.isBlank()) && (dueDateBefore == null || dueDateBefore.isBlank())
                && (isActive == null) && (frequency == null || frequency.isBlank())) {
            return super.getTransactionByCategoryFilteredByDate(
                    categoryName, beforeDate, afterDate, startDate, endDate, period, user, pageable);
        }
        subscriptionService.setOwnerId(user.getId());
        List<Subscription> subscriptions;

        try {
            if (dueDateBefore != null && !dueDateBefore.isBlank()) {
                subscriptions = subscriptionService.getByCategoryAndDueDateBefore(categoryName, LocalDate.parse(dueDateBefore));
            } else if (dueDateAfter != null && !dueDateAfter.isBlank()) {
                subscriptions = subscriptionService.getByCategoryAndDueDateAfter(categoryName, LocalDate.parse(dueDateAfter));
            } else if (dueDate != null && !dueDate.isBlank()) {
                subscriptions = subscriptionService.getByCategoryAndDueDate(categoryName, LocalDate.parse(dueDate));
            } else if (frequency != null && !frequency.isBlank()) {
                subscriptions = subscriptionService.getByFrequency(Frequency.valueOf(frequency.toUpperCase()));
            } else {
                subscriptions = subscriptionService.getByActive(isActive);
            }
            if (isActive != null) {
                subscriptions = subscriptions.stream().filter(subscription -> subscription.getIsActive() == isActive).toList();
            }

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.setTotal(subscriptions.size());
            response.addContent("subscriptions", subscriptions);

            logger.atInfo()
                    .addKeyValue("details",
                            Map.of("subscriptions_total", subscriptions.size()))
                    .log("[UUID={}] Transactions (subscriptions) retrieved successfully", user.getUuid());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (DateTimeParseException e) {
            throw new InvalidValueException("Invalid date format in the request! Should be : YYYY-MM-DD");
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException("Frequency must be one of these values " + Arrays.toString(Frequency.values()).toLowerCase());
        }
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

    @GetMapping("/export")
    public ResponseEntity<Resource> exportSubscriptions(
            @RequestParam(required = false, value = "type", defaultValue = "csv") String type,
            @RequestParam(required = false, value = "ids") Set<Long> ids,
            @AuthenticationPrincipal User user
    ) {
        return super.exportTransaction(type, ids, user);
    }
}
