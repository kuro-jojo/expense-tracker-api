package com.kuro.expensetracker.services.export;

import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.services.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Setter
@RequiredArgsConstructor
public class TransactionExportService<T extends Transaction> {
    private final TransactionService<T> transactionService;
    private final CSVTransactionWriter<T> csvTransactionWriter;
    private Class<T> transactionType;
    private Long ownerId;

    public ByteArrayResource generateCSV(Set<Long> transactionIds) {
        List<T> transactions = new ArrayList<>();
        transactionService.setType(transactionType);
        transactionService.setOwnerId(ownerId);
        transactionIds.forEach(id -> transactions.add(transactionService.getById(id)));
        return new ByteArrayResource(csvTransactionWriter.generateCSV(transactions, transactionType.getSimpleName()).toByteArray());
    }
}