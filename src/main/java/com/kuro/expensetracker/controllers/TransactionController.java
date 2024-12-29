package com.kuro.expensetracker.controllers;


import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.TransactionRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RequiredArgsConstructor
@Setter
public class TransactionController<T extends Transaction> {
    private final TransactionService<T> transactionService;
    private Class<T> type;


    public ResponseEntity<ApiResponse> createTransaction(TransactionRequest request, User user) {
        request.setOwner(user);
        T transaction = transactionService.create(request);

        ApiResponse response = new ApiResponse(true, HttpStatus.CREATED.value());

        response.setMessage(type.getSimpleName() + " has been created successfully");
        response.addContent(type.getSimpleName(), transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<ApiResponse> getTransactionById(String id, User user) {
        try {
            transactionService.setOwnerId(user.getId());
            var transaction = transactionService.getById(Long.valueOf(id));

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.addContent(type.getSimpleName().toLowerCase() + "s", transaction);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NumberFormatException e) {
            throw new InvalidValueException(type.getSimpleName() + "with invalid id provided");
        }
    }

    public ResponseEntity<ApiResponse> getTransactionByCategoryFilteredByDate(String categoryName, String beforeDate, String afterDate, String startDate, String endDate, User user) {
        try {
            transactionService.setOwnerId(user.getId());

            List<T> transactions;

            if (categoryName != null) {
                if (startDate != null && endDate != null) {
                    transactions = transactionService.getByCategoryAndDateBetween(categoryName, LocalDate.parse(startDate), LocalDate.parse(endDate));
                } else if (startDate != null) {
                    transactions = transactionService.getByCategoryAndDateBetween(categoryName, LocalDate.parse(startDate), LocalDate.now());
                } else if (endDate != null) {
                    transactions = transactionService.getByCategoryAndDateBetween(categoryName, LocalDate.now(), LocalDate.parse(endDate));
                } else if (beforeDate != null) {
                    transactions = transactionService.getByCategoryAndDateBefore(categoryName, LocalDate.parse(beforeDate));
                } else if (afterDate != null) {
                    transactions = transactionService.getByCategoryAndDateAfter(categoryName, LocalDate.parse(afterDate));
                } else {
                    transactions = transactionService.getByCategory(categoryName);
                }
            } else {
                if (startDate != null && endDate != null) {
                    transactions = transactionService.getByDateBetween(LocalDate.parse(startDate), LocalDate.parse(endDate));
                } else if (startDate != null) {
                    transactions = transactionService.getByDateBetween(LocalDate.parse(startDate), LocalDate.now());
                } else if (endDate != null) {
                    transactions = transactionService.getByDateBetween(LocalDate.now(), LocalDate.parse(endDate));
                } else if (beforeDate != null) {
                    transactions = transactionService.getByDateBefore(LocalDate.parse(beforeDate));
                } else if (afterDate != null) {
                    transactions = transactionService.getByDateAfter(LocalDate.parse(afterDate));
                } else {
                    transactions = transactionService.getAll();
                }
            }

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.setTotal(transactions.size());
            response.addContent(type.getSimpleName().toLowerCase() + "s", transactions);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (DateTimeParseException e) {
            throw new InvalidValueException("Invalid date format in the request! Should be : YYYY-MM-DD");
        }
    }

    public ResponseEntity<ApiResponse> updateTransactionById(String id, TransactionRequest request, User user) {
        try {
            if (request.isEmpty()) {

                ApiResponse response = new ApiResponse(false, HttpStatus.BAD_REQUEST.value());
                response.setMessage("No new value provided for update");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            request.setOwner(user);
            T transaction = transactionService.update(request, Long.valueOf(id));

            ApiResponse response = new ApiResponse(true, HttpStatus.CREATED.value());
            response.setMessage(type.getSimpleName() + " updated successfully");
            response.addContent(type.getSimpleName().toLowerCase(), transaction);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NumberFormatException e) {
            throw new InvalidValueException(type.getSimpleName() + "with invalid id provided");
        }
    }

    public ResponseEntity<ApiResponse> deleteTransactionById(String id, User user) {
        try {
            transactionService.setOwnerId(user.getId());
            transactionService.deleteById(Long.valueOf(id));
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NumberFormatException e) {
            throw new InvalidValueException(type.getSimpleName() + "with invalid id provided");
        }
    }

    public ResponseEntity<ApiResponse> getTotalOfTransactions(String startDate, String endDate, User user) {
        try {
            transactionService.setOwnerId(user.getId());
            BigDecimal total;
            if (startDate != null) {
                if (endDate != null) {
                    total = transactionService.getTotalBetween(LocalDate.parse(startDate), LocalDate.parse(endDate));
                } else {
                    total = transactionService.getTotalBetween(LocalDate.parse(startDate), LocalDate.now());
                }
            } else {
                total = transactionService.getTotal();
            }

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.addContent("total", total);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (DateTimeParseException e) {
            throw new InvalidValueException("Invalid date format in the request! Should be : YYYY-MM-DD");
        }
    }
}
