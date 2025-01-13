package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.models.Expense;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.ExpenseRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.export.TransactionExportService;
import com.kuro.expensetracker.services.transaction.expense.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("${api.prefix}/expenses")
public class ExpenseController extends TransactionController<Expense> {

    public ExpenseController(ExpenseService expenseService, TransactionExportService<Expense> transactionExportService) {
        super(expenseService, transactionExportService);

        transactionExportService.setTransactionType(Expense.class);
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
            @RequestParam(required = false, value = "period") String period,
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return super.getTransactionByCategoryFilteredByDate(
                categoryName, beforeDate, afterDate, startDate, endDate, period, user, pageable);
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

    @GetMapping("/export")
    public ResponseEntity<Resource> exportExpenses(
            @RequestParam(required = false, value = "type", defaultValue = "csv") String type,
            @RequestParam(required = false, value = "ids") Set<Long> ids,
            @AuthenticationPrincipal User user
    ) {
        return super.exportTransaction(type, ids, user);
    }
}