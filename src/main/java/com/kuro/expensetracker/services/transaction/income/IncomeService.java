package com.kuro.expensetracker.services.transaction.income;

import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Income;
import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.repositories.CategoryRepository;
import com.kuro.expensetracker.repositories.TransactionRepository;
import com.kuro.expensetracker.requests.TransactionRequest;
import com.kuro.expensetracker.services.transaction.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class IncomeService extends TransactionService {

    public IncomeService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        super(transactionRepository, categoryRepository);
    }

    public Income add(TransactionRequest request) throws InvalidValueException {
        var transaction =  super.add(request);
        return new Income(
                transaction.getId(),
                transaction.getTitle(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getTransactionDate(),
                transaction.getUser()
        );
    }
}
