package com.kuro.expensetracker.controllers;


import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.TransactionRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.transaction.TransactionService;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

//@RequiredArgsConstructor
@Setter
public class TransactionController<T extends Transaction> {
    private final TransactionService<T> transactionService;
    private final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private Class<T> type;

    public TransactionController(TransactionService<T> transactionService) {
        this.transactionService = transactionService;
    }

    public ResponseEntity<ApiResponse> createTransaction(TransactionRequest request, User user) {
        request.setOwner(user);
        T transaction = transactionService.create(request);

        ApiResponse response = new ApiResponse(true, HttpStatus.CREATED.value());

        response.setMessage("Transaction (" + getClassType() + ") created successfully");
        response.addContent(getClassType(), transaction);

        logger.atInfo()
                .addKeyValue("details",
                        Map.of(getClassType() + "_id", transaction.getId(),
                                getClassType() + "_title", transaction.getTitle()))
                .log("[UUID={}] Transaction ({}) created successfully", user.getUuid(), getClassType());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<ApiResponse> getTransactionById(String id, User user) {
        try {
            transactionService.setOwnerId(user.getId());
            var transaction = transactionService.getById(Long.valueOf(id));

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.addContent(type.getSimpleName().toLowerCase() + "s", transaction);

            logger.atInfo()
                    .addKeyValue("details",
                            Map.of(getClassType() + "_id", transaction.getId(),
                                    getClassType() + "_title", transaction.getTitle()))
                    .log("[UUID={}] Transaction ({}) retrieved successfully", user.getUuid(), getClassType());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NumberFormatException e) {
            throw new InvalidValueException(type.getSimpleName() + " with invalid id provided");
        }
    }

    public ResponseEntity<ApiResponse> getTransactionByCategoryFilteredByDate(
            String categoryName,
            String beforeDate,
            String afterDate,
            String startDate,
            String endDate,
            String period,
            User user,
            Pageable pageable) {
        try {
            transactionService.setOwnerId(user.getId());

            List<T> transactions;

            if (startDate != null && endDate != null && !startDate.isBlank() && !endDate.isBlank()) {
                transactions = transactionService.getByCategoryAndDateBetween(categoryName, LocalDate.parse(startDate), LocalDate.parse(endDate), pageable);
            } else if (startDate != null && !startDate.isBlank()) {
                transactions = transactionService.getByCategoryAndDateBetween(categoryName, LocalDate.parse(startDate), LocalDate.now(), pageable);
            } else if (endDate != null && !endDate.isBlank()) {
                transactions = transactionService.getByCategoryAndDateBetween(categoryName, LocalDate.now(), LocalDate.parse(endDate), pageable);
            } else if (beforeDate != null && !beforeDate.isBlank()) {
                transactions = transactionService.getByCategoryAndDateBefore(categoryName, LocalDate.parse(beforeDate), pageable);
            } else if (afterDate != null && !afterDate.isBlank()) {
                transactions = transactionService.getByCategoryAndDateAfter(categoryName, LocalDate.parse(afterDate), pageable);
            } else if (period != null && !period.isBlank()) {
                transactions = switch (period) {
                    case "today" -> transactionService.getByCategoryAndDateToday(categoryName, pageable);
                    case "week" -> transactionService.getByCategoryAndDateWeek(categoryName, pageable);
                    case "year" -> transactionService.getByCategoryAndDateYear(categoryName, pageable);
                    default -> transactionService.getByCategory(categoryName, pageable);
                };
            } else {
                transactions = transactionService.getByCategory(categoryName, pageable);
            }

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.setTotal(transactions.size());
            response.addContent(type.getSimpleName().toLowerCase() + "s", transactions);

            logger.atInfo()
                    .addKeyValue("details",
                            Map.of(getClassType() + "s_total", transactions.size()))
                    .log("[UUID={}] Transactions ({}s) retrieved successfully", user.getUuid(), getClassType());

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

            logger.atInfo()
                    .addKeyValue("details",
                            Map.of(getClassType() + "_id", transaction.getId(),
                                    getClassType() + "_title", transaction.getTitle()))
                    .log("[UUID={}] Transaction ({}) updated successfully", user.getUuid(), getClassType());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NumberFormatException e) {
            throw new InvalidValueException(type.getSimpleName() + " with invalid id provided");
        }
    }

    public ResponseEntity<ApiResponse> deleteTransactionById(String id, User user) {
        try {
            transactionService.setOwnerId(user.getId());
            transactionService.deleteById(Long.valueOf(id));

            logger.atInfo()
                    .addKeyValue("details",
                            Map.of(getClassType() + "_id", id))
                    .log("[UUID={}] Transaction ({}) created successfully", user.getUuid(), getClassType());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NumberFormatException e) {
            throw new InvalidValueException(type.getSimpleName() + " with invalid id provided");
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

            logger.atInfo()
                    .log("[UUID={}] Transactions ({}s) total retrieved successfully", user.getUuid(), getClassType());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (DateTimeParseException e) {
            throw new InvalidValueException("Invalid date format in the request! Should be : YYYY-MM-DD");
        }
    }

    private String getClassType() {
        return type.getSimpleName().toLowerCase();
    }
}
