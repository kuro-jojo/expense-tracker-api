package com.kuro.expensetracker.services.transaction.income;

import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Income;
import com.kuro.expensetracker.repositories.CategoryRepository;
import com.kuro.expensetracker.repositories.IncomeRepository;
import com.kuro.expensetracker.repositories.TransactionRepository;
import com.kuro.expensetracker.requests.TransactionRequest;
import com.kuro.expensetracker.services.transaction.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class IncomeService extends TransactionService {

    private final IncomeRepository incomeRepository;

    public IncomeService(TransactionRepository transactionRepository, CategoryRepository categoryRepository, IncomeRepository incomeRepository) {
        super(transactionRepository, categoryRepository);
        this.incomeRepository = incomeRepository;
    }

    public Income create(TransactionRequest request) throws InvalidValueException {
        var transaction = super.create(request);
        var income = new Income(
                transaction.getId(),
                transaction.getTitle(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getTransactionDate(),
                transaction.getOwner()
        );
        return incomeRepository.save(income);
    }
}
