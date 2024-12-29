package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Expense;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.ExpenseRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.transaction.expense.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping()
    public ResponseEntity<ApiResponse> createExpense(@RequestBody @Valid ExpenseRequest request, @AuthenticationPrincipal User user) {
        request.setOwner(user);
        Expense expense = expenseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Expense created successfully", expense));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getExpenseById(@PathVariable String id, @AuthenticationPrincipal User user) {
        try {
            expenseService.setOwnerId(user.getId());
            var expense = expenseService.getById(Long.valueOf(id));
            return ResponseEntity.ok(new ApiResponse(expense));
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid expense id provided!");
        }

    }

    @GetMapping("")
    public ResponseEntity<ApiResponse> getExpensesByField(
            @RequestParam(required = false, value = "f") String field,
            @RequestParam(required = false, value = "v") String value,
            @RequestParam(required = false, value = "v2") String value2,
            @AuthenticationPrincipal User user) {
        try {
            expenseService.setOwnerId(user.getId());

            List<Expense> expenses;
            if (field == null) {
                expenses = expenseService.getAll();
            } else {
                switch (field) {
                    case "category": // assume it is the category name
                        expenses = expenseService.getByCategory(value);
                        break;
                    case "date":
                        expenses = expenseService.getByTransactionDate(LocalDate.parse(value));
                        break;
                    case "before":
                        expenses = expenseService.getBeforeDate(LocalDate.parse(value));
                        break;
                    case "after":
                        expenses = expenseService.getAfterDate(LocalDate.parse(value));
                        break;
                    case "between":
                        if (value2 == null) {
                            expenses = expenseService.getBetweenDate(LocalDate.parse(value), LocalDate.now());
                        } else {
                            expenses = expenseService.getBetweenDate(LocalDate.parse(value), LocalDate.parse(value2));
                        }
                        break;
                    default:
                        expenses = expenseService.getAll();
                }
            }
            return ResponseEntity.ok(new ApiResponse(expenses, expenses.size()));
        } catch (DateTimeParseException e) {
            throw new InvalidValueException("Invalid date format in the request! Should be : YYYY-MM-DD");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateExpenseById(
            @PathVariable String id,
            @RequestBody ExpenseRequest request,
            @AuthenticationPrincipal User user
    ) {
        try {
            if (request.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("No new value provided for update"));
            }
            request.setOwner(user);
            var expense = expenseService.update(request, Long.valueOf(id));
            return ResponseEntity.ok(new ApiResponse("Expense updated successfully", expense));
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid expense id provided!");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteExpenseById(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        try {
            expenseService.setOwnerId(user.getId());
            expenseService.deleteById(Long.valueOf(id));
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid expense id provided!");
        }
    }

    @GetMapping("total")
    public ResponseEntity<ApiResponse> getTotalExpenses(
            @RequestParam(required = false, value = "s") String startDate,
            @RequestParam(required = false, value = "e") String endDate,
            @AuthenticationPrincipal User user
    ) {
        try {

            expenseService.setOwnerId(user.getId());
            BigDecimal total;
            if (startDate != null) {
                if (endDate != null) {
                    total = expenseService.getTotalBetween(LocalDateTime.parse(startDate), LocalDateTime.parse(endDate));
                } else {
                    total = expenseService.getTotalBetween(LocalDateTime.parse(startDate), LocalDateTime.now());
                }
            } else {
                total = expenseService.getTotal();
            }
            return ResponseEntity.ok(new ApiResponse(total));
        } catch (DateTimeParseException e) {
            throw new InvalidValueException("Invalid date format in the request! Should be : YYYY-MM-DD");
        }
    }
}