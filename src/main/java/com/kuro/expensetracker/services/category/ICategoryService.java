package com.kuro.expensetracker.services.category;

import com.kuro.expensetracker.exceptions.EntityAlreadyPresentException;
import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.requests.CategoryRequest;

import java.util.List;

public interface ICategoryService {
    Category create(CategoryRequest request) throws EntityAlreadyPresentException, InvalidValueException;

    Category update(CategoryRequest request, Long categoryId) throws EntityNotFoundException, InvalidValueException;

    void deleteById(Long id) throws EntityNotFoundException;

    Category getByName(String name);

    Category getById(Long id) throws EntityNotFoundException;

    List<Category> getAll();

    List<String> getAllWithNameOnly();
}
