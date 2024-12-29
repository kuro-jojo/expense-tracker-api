package com.kuro.expensetracker.services.transaction.income;

import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Income;
import com.kuro.expensetracker.models.Transaction;
import com.kuro.expensetracker.repositories.CategoryRepository;
import com.kuro.expensetracker.repositories.IncomeRepository;
import com.kuro.expensetracker.repositories.TransactionRepository;
import com.kuro.expensetracker.requests.IncomeRequest;
import com.kuro.expensetracker.requests.TransactionRequest;
import com.kuro.expensetracker.services.transaction.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class IncomeService extends TransactionService implements IIncomeService {

    private final IncomeRepository incomeRepository;

    public IncomeService(TransactionRepository transactionRepository, CategoryRepository categoryRepository, com.kuro.expensetracker.repositories.IncomeRepository incomeRepository) {
        super(transactionRepository, categoryRepository);
        this.incomeRepository = incomeRepository;
    }

    @Transactional
    public Income create(TransactionRequest request) throws InvalidValueException {
        var transaction = super.create(request);
        var income = getIncomeFromTransaction(transaction);
        return incomeRepository.save(income);
    }

    @Override
    public Income update(IncomeRequest request, Long incomeId) throws EntityNotFoundException {
        return incomeRepository.findByIdAndOwnerId(incomeId, request.getOwner().getId())
                .map(existingTransaction -> getIncomeFromTransaction(super.update(existingTransaction, request)))
                .map(transactionRepository::save)
                .orElseThrow(() -> new EntityNotFoundException(Income.class, incomeId));
    }

    private Income getIncomeFromTransaction(Transaction transaction) {
        return new Income(
                transaction.getId(),
                transaction.getTitle(),
                transaction.getDescription(),
                transaction.getAmount().signum() < 0 ? transaction.getAmount().negate() : transaction.getAmount(),
                transaction.getCategory(),
                transaction.getTransactionDate(),
                transaction.getOwner()
        );
    }

    @Override
    public Income getById(Long id) throws EntityNotFoundException {
        return incomeRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new EntityNotFoundException(Income.class, id));
    }

    @Override
    public List<Income> getAll() {
        return incomeRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Income> getByCategory(Long categoryId) {
        return incomeRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Income> getByCategory(String categoryName) {
        return incomeRepository.findByCategoryName(categoryName);
    }

    @Override
    public List<Income> getByTransactionDate(LocalDate date) {
        return incomeRepository.findByTransactionDate(date);
    }

    @Override
    public List<Income> getBeforeDate(LocalDate date) {
        return incomeRepository.findByTransactionDateBefore(date);
    }

    @Override
    public List<Income> getAfterDate(LocalDate date) {
        return incomeRepository.findByTransactionDateAfter(date);
    }

    @Override
    public List<Income> getBetweenDate(LocalDate minDate, LocalDate maxDate) {
        return incomeRepository.findByTransactionDateBetween(minDate, maxDate);
    }

    @Override
    public BigDecimal getTotal() {
        return incomeRepository.findTotalIncomesByOwnerId(ownerId).orElse(new BigDecimal(0));
    }

    @Override
    public BigDecimal getTotalBetween(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findTotalIncomesByOwnerIdBetween(ownerId,
                LocalDateTime.of(startDate, LocalTime.of(23, 59, 59)),
                LocalDateTime.of(endDate, LocalTime.of(23, 59, 59))).orElse(new BigDecimal(0));
    }
}
