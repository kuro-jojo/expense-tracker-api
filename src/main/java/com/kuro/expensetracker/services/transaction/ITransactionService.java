package com.kuro.expensetracker.services.transaction;

import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.requests.TransactionRequest;

public interface ITransactionService {
    Transaction create(TransactionRequest request);

    Transaction update(Transaction existingTransaction, TransactionRequest request) throws EntityNotFoundException;

    void deleteById(Long id) throws EntityNotFoundException;


}
