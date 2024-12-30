package com.kuro.expensetracker.services.category;

import com.kuro.expensetracker.exceptions.EntityAlreadyPresentException;
import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.repositories.CategoryRepository;
import com.kuro.expensetracker.requests.CategoryRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@RequiredArgsConstructor
@Service
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private Long ownerId;

    @Override
    public Category create(CategoryRequest request) throws EntityAlreadyPresentException, InvalidValueException {
        request.setName(request.getName().toLowerCase());
        categoryRepository.findByNameAndOwnerId(request.getName(), request.getOwner().getId())
                .ifPresent(category -> {
                            throw new EntityAlreadyPresentException(Category.class, request.getName());
                        }
                );

        if (request.getThreshold() != null && request.getThreshold().signum() < 0) {
            throw new InvalidValueException("Threshold must be greater than zero!");
        }

        return categoryRepository.save(
                new Category(
                        request.getName(),
                        request.getDescription(),
                        request.getThreshold(),
                        request.getTransactions(),
                        request.getOwner()
                ));
    }

    @Override
    public Category update(CategoryRequest request, Long categoryId) throws EntityNotFoundException, InvalidValueException {
        request.setName(request.getName().toLowerCase());
        return categoryRepository.findByIdAndOwnerId(categoryId, request.getOwner().getId())
                .map(existingCategory -> updateCategory(existingCategory, request))
                .map(categoryRepository::save)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, categoryId));
    }

    private Category updateCategory(Category existingCategory, CategoryRequest request) throws InvalidValueException {
        if (request.getThreshold() != null && request.getThreshold().signum() < 0) {
            throw new InvalidValueException("Threshold must be greater than zero!");
        }
        if (request.getName() != null && !request.getName().isBlank()) {
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
        categoryRepository.findByIdAndOwnerId(id, ownerId)
                .ifPresentOrElse(categoryRepository::delete,
                        () -> {
                            throw new EntityNotFoundException(Category.class, id);
                        }
                );
    }

    @Override
    public Category getByName(String name) {
        return categoryRepository.findByNameAndOwnerId(name, ownerId).orElseThrow(
                () -> new EntityNotFoundException(Category.class, name)
        );
    }

    @Override
    public Category getById(Long id) {
        return categoryRepository.findByIdAndOwnerId(id, ownerId).orElseThrow(
                () -> new EntityNotFoundException(Category.class, id)
        );
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Category> getAllWithNameOnly() {
        var categories = categoryRepository.findByOwnerId(ownerId);

        return categories.stream()
                .map(category -> new Category(category.getName()))
                .toList();
    }
}
