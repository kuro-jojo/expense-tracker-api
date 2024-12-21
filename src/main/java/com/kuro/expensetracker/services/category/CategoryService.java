package com.kuro.expensetracker.services.category;

import com.kuro.expensetracker.exceptions.EntityAlreadyPresentException;
import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.repositories.CategoryRepository;
import com.kuro.expensetracker.requests.CategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category add(CategoryRequest request) throws EntityAlreadyPresentException, InvalidValueException {
        categoryRepository.findByName(request.getName()).ifPresent(
                category -> {
                    throw new EntityAlreadyPresentException(Category.class, request.getName());
                }
        );

        if (request.getThreshold() != null && request.getThreshold() < 0) {
            throw new InvalidValueException("Threshold must be greater than zero!");
        }

        return categoryRepository.save(
                new Category(
                        request.getName(),
                        request.getDescription(),
                        request.getThreshold(),
                        request.getTransactions()
                ));
    }

    @Override
    public Category update(CategoryRequest request, Long categoryId) throws EntityNotFoundException {
        return categoryRepository.findById(categoryId)
                .map(existingCategory -> updateCategory(existingCategory, request))
                .map(categoryRepository::save)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, categoryId));
    }

    private Category updateCategory(Category existingCategory, CategoryRequest request) throws InvalidValueException {
        if (request.getThreshold() != null && request.getThreshold() < 0) {
            throw new InvalidValueException("Threshold must be greater than zero!");
        }
        if (request.getName() != null) {
            existingCategory.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existingCategory.setDescription(request.getDescription());
        }
        if (request.getThreshold() != null) {
            existingCategory.setThreshold(request.getThreshold());
        }
        if (request.getTransactions() != null) {
            existingCategory.setTransactions(request.getTransactions());
        }

        return existingCategory;
    }

    @Override
    public void deleteById(Long id) throws EntityNotFoundException {
        categoryRepository.findById(id)
                .ifPresentOrElse(categoryRepository::delete,
                        () -> {
                            throw new EntityNotFoundException(Category.class, id);
                        }
                );
    }

    @Override
    public Category getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(Category.class, id)
        );
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
}
