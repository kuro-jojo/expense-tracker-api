package com.kuro.expensetracker.config;

import com.kuro.expensetracker.models.Expense;
import com.kuro.expensetracker.models.Income;
import com.kuro.expensetracker.models.Subscription;
import com.kuro.expensetracker.services.export.CSVTransactionWriter;
import com.kuro.expensetracker.services.export.TransactionExportService;
import com.kuro.expensetracker.services.transaction.expense.ExpenseService;
import com.kuro.expensetracker.services.transaction.income.IncomeService;
import com.kuro.expensetracker.services.transaction.subscription.SubscriptionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExportServiceConfig {

    @Bean
    public TransactionExportService<Income> incomeExportService(IncomeService incomeService, CSVTransactionWriter<Income> csvTransactionWriter) {
        return new TransactionExportService<>(incomeService, csvTransactionWriter);
    }

    @Bean
    public TransactionExportService<Expense> expenseExportService(ExpenseService expenseService, CSVTransactionWriter<Expense> csvTransactionWriter) {
        return new TransactionExportService<>(expenseService, csvTransactionWriter);
    }

    @Bean
    public TransactionExportService<Subscription> subscriptionExportService(SubscriptionService subscriptionService, CSVTransactionWriter<Subscription> csvTransactionWriter) {
        return new TransactionExportService<>(subscriptionService, csvTransactionWriter);
    }
}
