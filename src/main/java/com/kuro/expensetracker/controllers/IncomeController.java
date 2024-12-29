package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Income;
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

import java.math.BigDecimal;
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

        ApiResponse response = new ApiResponse(true, HttpStatus.CREATED.value());
        response.setMessage("Income created successfully");
        response.addContent("income", income);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getIncomeById(@PathVariable String id, @AuthenticationPrincipal User user) {
        try {
            incomeService.setOwnerId(user.getId());
            var income = incomeService.getById(Long.valueOf(id));

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.addContent("income", income);
            return ResponseEntity.status(HttpStatus.OK).body(response);
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

            List<Income> incomes;
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
                        incomes = incomeService.getBeforeDate(LocalDate.parse(value));
                        break;
                    case "after":
                        incomes = incomeService.getAfterDate(LocalDate.parse(value));
                        break;
                    case "between":
                        if (value2 == null) {
                            incomes = incomeService.getBetweenDate(LocalDate.parse(value), LocalDate.now());
                        } else {
                            incomes = incomeService.getBetweenDate(LocalDate.parse(value), LocalDate.parse(value2));
                        }
                        break;
                    default:
                        incomes = incomeService.getAll();
                }
            }

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.setTotal(incomes.size());
            response.addContent("incomes", incomes);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (DateTimeParseException e) {
            throw new InvalidValueException("Invalid date format in the request! Should be : YYYY-MM-DD");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateIncomeById(
            @PathVariable String id,
            @RequestBody IncomeRequest request,
            @AuthenticationPrincipal User user
    ) {
        try {
            if (request.isEmpty()) {
                ApiResponse response = new ApiResponse(false, HttpStatus.BAD_REQUEST.value());
                response.setMessage("No new value provided for update");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            request.setOwner(user);
            var income = incomeService.update(request, Long.valueOf(id));

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.setMessage("Income updated successfully");
            response.addContent("income", income);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid income id provided!");
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

    @GetMapping("total")
    public ResponseEntity<ApiResponse> getTotalIncomes(
            @RequestParam(required = false, value = "s") String startDate,
            @RequestParam(required = false, value = "e") String endDate,
            @AuthenticationPrincipal User user
    ) {
        try {

            incomeService.setOwnerId(user.getId());
            BigDecimal total;
            if (startDate != null) {
                if (endDate != null) {
                    total = incomeService.getTotalBetween(LocalDate.parse(startDate), LocalDate.parse(endDate));
                } else {
                    total = incomeService.getTotalBetween(LocalDate.parse(startDate), LocalDate.now());
                }
            } else {
                total = incomeService.getTotal();
            }

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.addContent("total", total);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (DateTimeParseException e) {
            throw new InvalidValueException("Invalid date format in the request! Should be : YYYY-MM-DD");
        }
    }
}
