package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.models.Expense;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.ExpenseRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.transaction.expense.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/expenses")
public class ExpenseController extends TransactionController<Expense> {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        super(expenseService);
        this.expenseService = expenseService;
        setType(Expense.class);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> createExpense(@RequestBody @Valid ExpenseRequest request, @AuthenticationPrincipal User user) {
        return super.createTransaction(request, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getExpenseById(@PathVariable String id, @AuthenticationPrincipal User user) {
        return super.getTransactionById(id, user);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse> getExpensesByCategoryFilteredByDate(
            @RequestParam(required = false, value = "c") String categoryName,
            @RequestParam(required = false, value = "b") String beforeDate,
            @RequestParam(required = false, value = "a") String afterDate,
            @RequestParam(required = false, value = "s") String startDate,
            @RequestParam(required = false, value = "e") String endDate,
            @AuthenticationPrincipal User user) {
        return super.getTransactionByCategoryFilteredByDate(
                categoryName, beforeDate, afterDate, startDate, endDate, user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateExpenseById(
            @PathVariable String id,
            @RequestBody ExpenseRequest request,
            @AuthenticationPrincipal User user
    ) {
        return super.updateTransactionById(id, request, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteExpenseById(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        return super.deleteTransactionById(id, user);
    }

    @GetMapping("total")
    public ResponseEntity<ApiResponse> getTotalOfExpenses(
            @RequestParam(required = false, value = "s") String startDate,
            @RequestParam(required = false, value = "e") String endDate,
            @AuthenticationPrincipal User user
    ) {
        return super.getTotalOfTransactions(startDate, endDate, user);
    }

}