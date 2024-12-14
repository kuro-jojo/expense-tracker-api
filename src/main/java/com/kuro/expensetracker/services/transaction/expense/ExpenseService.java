package com.kuro.expensetracker.services.transaction.expense;

import com.kuro.expensetracker.repositories.CategoryRepository;
import com.kuro.expensetracker.repositories.TransactionRepository;
import com.kuro.expensetracker.services.transaction.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService extends TransactionService {

    public ExpenseService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        super(transactionRepository, categoryRepository);
    }
}
