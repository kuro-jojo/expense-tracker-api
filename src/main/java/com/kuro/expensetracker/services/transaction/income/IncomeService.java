package com.kuro.expensetracker.services.transaction.income;

import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.models.Income;
import com.kuro.expensetracker.repositories.IncomeRepository;
import com.kuro.expensetracker.requests.TransactionRequest;
import com.kuro.expensetracker.services.category.CategoryService;
import com.kuro.expensetracker.services.transaction.TransactionCategorizationService;
import com.kuro.expensetracker.services.transaction.TransactionService;
import com.kuro.expensetracker.utils.DateTimeUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class IncomeService extends TransactionService<Income> {

    private final IncomeRepository incomeRepository;

    public IncomeService(
            CategoryService categoryService,
            IncomeRepository incomeRepository,
            TransactionCategorizationService transactionCategorizationService) {
        super(incomeRepository, categoryService, transactionCategorizationService);
        this.incomeRepository = incomeRepository;
        setType(Income.class);
    }

    @Transactional
    @Override
    public Income create(TransactionRequest request) throws InvalidValueException {
        var income = super.create(request);
        return incomeRepository.save(income);
    }

    @Override
    public Income createFromRequest(TransactionRequest request, Category category) {
        return new Income(
                request.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getAmount().signum() < 0 ? request.getAmount().negate() : request.getAmount(),
                category,
                request.getTransactionDate(),
                request.getOwner()
        );
    }

    @Override
    public Income update(TransactionRequest request, Long incomeId) throws EntityNotFoundException {
        var income = super.update(request, incomeId);
        return incomeRepository.save(income);
    }

    @Override
    public BigDecimal getTotal() {
        return incomeRepository.findTotalIncomesByOwnerId(ownerId).orElse(new BigDecimal(0));
    }

    @Override
    public BigDecimal getTotalBetween(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findTotalIncomesByOwnerIdBetween(ownerId,
                DateTimeUtil.getStartOfDay(startDate),
                DateTimeUtil.getEndOfDay(endDate)).orElse(new BigDecimal(0));
    }
}