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
    // TODO : add validation
    public ResponseEntity<ApiResponse> addIncome(@RequestBody @Valid IncomeRequest request) {
        // TODO : replace by current user
        request.setUser(new User(1L));
        Income income = incomeService.add(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Income created successfully", income));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getIncome(@PathVariable String id) {
        try {
            var income = incomeService.getById(Long.valueOf(id));
            return ResponseEntity.ok(new ApiResponse("Income found!", income));
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid income id provided!");
        }

    }

    @GetMapping("")
    public ResponseEntity<ApiResponse> getIncomesBy(@RequestParam(required = false) String field, @RequestParam(required = false) String value, @RequestParam(required = false) String value2) {
        try {
            List<Transaction> incomes;
            if (field == null) {
                // TODO : retrieve the current user
                incomes = incomeService.getByUser(1L);
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
                        // TODO : retrieve the current user
                        incomes = incomeService.getByUser(1L);
                }
            }
            return ResponseEntity.ok(new ApiResponse(incomes.isEmpty() ? "No incomes found!" : "Incomes found!", incomes, incomes.size()));
        } catch (DateTimeParseException e) {
            throw new InvalidValueException("Invalid date format! Should be : YYYY-MM-DD");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteIncome(@PathVariable String id) {
        try {
            incomeService.deleteById(Long.valueOf(id));
            return ResponseEntity.ok(new ApiResponse("Income with id #" + id + " deleted successfully!"));

        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid income id provided!");
        }
    }

}
