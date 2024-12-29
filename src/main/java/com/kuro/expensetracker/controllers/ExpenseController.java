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

        ApiResponse response = new ApiResponse(true, HttpStatus.CREATED.value());
        response.setMessage("Expense created successfully");
        response.addContent("expense", expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getExpenseById(@PathVariable String id, @AuthenticationPrincipal User user) {
        try {
            expenseService.setOwnerId(user.getId());
            var expense = expenseService.getById(Long.valueOf(id));

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.addContent("expense", expense);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid expense id provided!");
        }

    }

    @GetMapping("")
    public ResponseEntity<ApiResponse> getExpensesByCategoryNameFilteredByTransactionDate(
            @RequestParam(required = false, value = "c") String categoryName,
            @RequestParam(required = false, value = "b") String beforeDate,
            @RequestParam(required = false, value = "a") String afterDate,
            @RequestParam(required = false, value = "s") String startDate,
            @RequestParam(required = false, value = "e") String endDate,
            @AuthenticationPrincipal User user) {
        try {
            expenseService.setOwnerId(user.getId());

            List<Expense> expenses;

            if (categoryName != null) {
                if (startDate != null && endDate != null) {
                    expenses = expenseService.getByCategoryAndDateBetween(categoryName, LocalDate.parse(startDate), LocalDate.parse(endDate));
                } else if (startDate != null) {
                    expenses = expenseService.getByCategoryAndDateBetween(categoryName, LocalDate.parse(startDate), LocalDate.now());
                } else if (endDate != null) {
                    expenses = expenseService.getByCategoryAndDateBetween(categoryName, LocalDate.now(), LocalDate.parse(endDate));
                } else if (beforeDate != null) {
                    expenses = expenseService.getByCategoryAndDateBefore(categoryName, LocalDate.parse(beforeDate));
                } else if (afterDate != null) {
                    expenses = expenseService.getByCategoryAndDateAfter(categoryName, LocalDate.parse(afterDate));
                } else {
                    expenses = expenseService.getByCategory(categoryName);
                }
            } else {
                if (startDate != null && endDate != null) {
                    expenses = expenseService.getByDateBetween(LocalDate.parse(startDate), LocalDate.parse(endDate));
                } else if (startDate != null) {
                    expenses = expenseService.getByDateBetween(LocalDate.parse(startDate), LocalDate.now());
                } else if (endDate != null) {
                    expenses = expenseService.getByDateBetween(LocalDate.now(), LocalDate.parse(endDate));
                } else if (beforeDate != null) {
                    expenses = expenseService.getByDateBefore(LocalDate.parse(beforeDate));
                } else if (afterDate != null) {
                    expenses = expenseService.getByDateAfter(LocalDate.parse(afterDate));
                } else {
                    expenses = expenseService.getAll();
                }
            }

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.setTotal(expenses.size());
            response.addContent("expenses", expenses);
            return ResponseEntity.status(HttpStatus.OK).body(response);
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

                ApiResponse response = new ApiResponse(false, HttpStatus.BAD_REQUEST.value());
                response.setMessage("No new value provided for update");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            request.setOwner(user);
            var expense = expenseService.update(request, Long.valueOf(id));

            ApiResponse response = new ApiResponse(true, HttpStatus.CREATED.value());
            response.setMessage("Expense updated successfully");
            response.addContent("expense", expense);
            return ResponseEntity.status(HttpStatus.OK).body(response);
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
                    total = expenseService.getTotalBetween(LocalDate.parse(startDate), LocalDate.parse(endDate));
                } else {
                    total = expenseService.getTotalBetween(LocalDate.parse(startDate), LocalDate.now());
                }
            } else {
                total = expenseService.getTotal();
            }

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.addContent("total", total);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (DateTimeParseException e) {
            throw new InvalidValueException("Invalid date format in the request! Should be : YYYY-MM-DD");
        }
    }
}