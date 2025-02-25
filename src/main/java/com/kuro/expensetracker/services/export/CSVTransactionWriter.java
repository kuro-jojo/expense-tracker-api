package com.kuro.expensetracker.services.export;

import com.kuro.expensetracker.models.Subscription;
import com.kuro.expensetracker.models.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CSVTransactionWriter<T extends Transaction> {
    private final String[] HEADERS = {
            "TYPE",
            "ID",
            "TITLE",
            "AMOUNT",
            "DESCRIPTION",
            "TRANSACTION DATE",
            "CATEGORY NAME",
            "DUE DATE",
            "FREQUENCY",
            "ACTIVE",
    };

    public ByteArrayOutputStream generateCSV(List<T> transactions, String type) {

        CSVFormat csvFormat = CSVFormat.EXCEL.builder()
                .setHeader(HEADERS)
                .build();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (CSVPrinter printer = new CSVPrinter(new PrintWriter(outputStream), csvFormat)) {
            for (T transaction : transactions) {
                List<Object> record = new ArrayList<>();
                record.add(type.toUpperCase());
                record.add(transaction.getId());
                record.add(transaction.getTitle());
                record.add(transaction.getAmount());
                record.add(transaction.getDescription());
                record.add(transaction.getTransactionDate());
                record.add(transaction.getCategory().getName());

                // Add specific fields for Subscription if it's an instance of Subscription
                if (transaction instanceof Subscription subscription) {
                    record.add(subscription.getDueDate());
                    record.add(subscription.getFrequency());
                    record.add(subscription.getIsActive());
                }

                printer.printRecord(record);
                printer.println();
            }
            return outputStream;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
