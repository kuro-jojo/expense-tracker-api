package com.kuro.expensetracker.services.transaction;

import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.repositories.CategoryRepository;
import com.kuro.expensetracker.repositories.TransactionRepository;
import com.kuro.expensetracker.requests.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {
    protected final TransactionRepository transactionRepository;
    protected final CategoryRepository categoryRepository;

    @Override
    public Transaction add(TransactionRequest request) {
        Category category = categoryRepository.findByName(request.getCategory().getName())
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        transactionRepository.save(createTransaction(request));
        return null;
    }

    private Transaction createTransaction(TransactionRequest request) {
        return new Transaction(
                request.getTitle(),
                request.getDescription(),
                request.getAmount(),
                request.getCategory(),
                request.getTransactionDate(),
                request.getUser()
        );
    }

    @Override
    public Transaction update(TransactionRequest request, Long transactionId) throws EntityNotFoundException {

        return transactionRepository.findById(transactionId)
                .map(existingTransaction -> updateTransaction(existingTransaction, request))
                .map(transactionRepository::save)
                .orElseThrow(() -> new EntityNotFoundException(Transaction.class, transactionId));
    }

    private Transaction updateTransaction(Transaction existingTransaction, TransactionRequest request) throws EntityNotFoundException{
        existingTransaction.setTitle(request.getTitle());
        existingTransaction.setDescription(request.getDescription());
        existingTransaction.setTransactionDate(request.getTransactionDate());
        existingTransaction.setAmount(request.getAmount());

        categoryRepository.findByName(request.getCategory().getName()).ifPresentOrElse(
                existingTransaction::setCategory,
                () -> {
                    throw new EntityNotFoundException(Category.class, request.getCategory().getName());
                }
        );
        return existingTransaction;
    }

    @Override
    public void deleteById(Long id)  throws EntityNotFoundException{
        transactionRepository.findById(id)
                .ifPresentOrElse(transactionRepository::delete,
                        () -> {
                            throw new EntityNotFoundException(Transaction.class, id);
                        });
    }

    @Override
    public Optional<Transaction> getById(Long id)  throws EntityNotFoundException{
        return Optional.ofNullable(transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Transaction.class, id)));
    }

    @Override
    public List<Transaction> getByUser(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Override
    public List<Transaction> getByCategory(Long categoryId) {
        return transactionRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Transaction> getByCategory(String categoryName) {
        return transactionRepository.findByCategoryName(categoryName);
    }

    @Override
    public List<Transaction> getBefore(LocalDate date) {
        return transactionRepository.findByTransactionDateBefore(date);
    }

    @Override
    public List<Transaction> getAfter(LocalDate date) {
        return transactionRepository.findByTransactionDateAfter(date);
    }

    @Override
    public List<Transaction> getBetween(LocalDate minDate, LocalDate maxDate) {
        return transactionRepository.findByTransactionDateBetween(minDate, maxDate);
    }
}
