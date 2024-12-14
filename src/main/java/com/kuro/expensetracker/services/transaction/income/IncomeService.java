package com.kuro.expensetracker.services.transaction.income;

import com.kuro.expensetracker.repositories.CategoryRepository;
import com.kuro.expensetracker.repositories.TransactionRepository;
import com.kuro.expensetracker.services.transaction.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class IncomeService extends TransactionService {

    public IncomeService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        super(transactionRepository, categoryRepository);
    }
}
