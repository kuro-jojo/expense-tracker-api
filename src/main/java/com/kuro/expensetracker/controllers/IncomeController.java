package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.models.Income;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.IncomeRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.transaction.income.IncomeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/incomes")
public class IncomeController extends TransactionController<Income> {
    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        super(incomeService);
        this.incomeService = incomeService;
        setType(Income.class);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> createIncome(@RequestBody @Valid IncomeRequest request, @AuthenticationPrincipal User user) {
        return super.createTransaction(request, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getIncomeById(@PathVariable String id, @AuthenticationPrincipal User user) {
        return super.getTransactionById(id, user);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse> getIncomesByField(
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
    public ResponseEntity<ApiResponse> updateIncomeById(
            @PathVariable String id,
            @RequestBody IncomeRequest request,
            @AuthenticationPrincipal User user
    ) {
        return super.updateTransactionById(id, request, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteIncomeById(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        return super.deleteTransactionById(id, user);
    }

    @GetMapping("total")
    public ResponseEntity<ApiResponse> getTotalOfIncomes(
            @RequestParam(required = false, value = "s") String startDate,
            @RequestParam(required = false, value = "e") String endDate,
            @AuthenticationPrincipal User user
    ) {
        return super.getTotalOfTransactions(startDate, endDate, user);
    }
}
