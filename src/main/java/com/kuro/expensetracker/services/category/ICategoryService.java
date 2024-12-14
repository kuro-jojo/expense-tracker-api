package com.kuro.expensetracker.services.category;

import com.kuro.expensetracker.exceptions.EntityAlreadyPresentException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.requests.CategoryRequest;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {
    Category add(CategoryRequest request) throws EntityAlreadyPresentException, InvalidValueException;

    Category update(CategoryRequest request, Long categoryId);

    void deleteById(Long id);

    Optional<Category> getById(Long id);

    List<Category> getAll();
}
