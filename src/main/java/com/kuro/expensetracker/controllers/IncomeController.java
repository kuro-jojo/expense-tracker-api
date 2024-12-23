package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Income;
import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.IncomeRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.transaction.income.IncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/incomes")
public class IncomeController {
    private final IncomeService incomeService;

    @PostMapping()
    public ResponseEntity<ApiResponse> createIncome(@RequestBody @Valid IncomeRequest request, @AuthenticationPrincipal User user) {
        request.setOwner(user);
        Income income = incomeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Income created successfully", income));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getIncomeById(@PathVariable String id, @AuthenticationPrincipal User user) {
        try {
            incomeService.setOwnerId(user.getId());
            var income = incomeService.getById(Long.valueOf(id));
            return ResponseEntity.ok(new ApiResponse(income));
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid income id provided!");
        }

    }

    @GetMapping("")
    public ResponseEntity<ApiResponse> getIncomesByField(
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String value,
            @RequestParam(required = false) String value2,
            @AuthenticationPrincipal User user) {
        try {
            incomeService.setOwnerId(user.getId());

            List<Transaction> incomes;
            if (field == null) {
                incomes = incomeService.getAll();
            } else {
                switch (field) {
                    case "category": // assume it is the category name
                        incomes = incomeService.getByCategory(value);
                        break;
                    case "date":
                        incomes = incomeService.getByTransactionDate(LocalDate.parse(value));
                        break;
                    case "before":
                        incomes = incomeService.getBefore(LocalDate.parse(value));
                        break;
                    case "after":
                        incomes = incomeService.getAfter(LocalDate.parse(value));
                        break;
                    case "between":
                        if (value2 == null) {
                            incomes = incomeService.getBetween(LocalDate.parse(value), LocalDate.now());
                        } else {
                            incomes = incomeService.getBetween(LocalDate.parse(value), LocalDate.parse(value2));
                        }
                        break;
                    default:
                        incomes = incomeService.getAll();
                }
            }
            return ResponseEntity.ok(new ApiResponse(incomes, incomes.size()));
        } catch (DateTimeParseException e) {
            throw new InvalidValueException("Invalid date format in the request! Should be : YYYY-MM-DD");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteIncomeById(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        try {
            incomeService.setOwnerId(user.getId());
            incomeService.deleteById(Long.valueOf(id));
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid income id provided!");
        }
    }

}
