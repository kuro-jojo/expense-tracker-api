package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.CategoryRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping()
    public ResponseEntity<ApiResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User user) {
        categoryService.setOwnerId(user.getId());
        ApiResponse response = new ApiResponse(true, HttpStatus.CREATED.value());
        response.setMessage("Category added successfully");
        response.addContent("category", categoryService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse> getAllCategories(
            @RequestParam(required = false) boolean namesOnly,
            @AuthenticationPrincipal User user) {
        categoryService.setOwnerId(user.getId());
        List<Category> categories;
        if (namesOnly) {
            categories = categoryService.getAllWithNameOnly();
        } else {
            categories = categoryService.getAll();
        }

        ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
        response.setTotal(categories.size());
        response.addContent("categories", categories);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategory(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        try {
            categoryService.setOwnerId(user.getId());
            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.addContent("category", categoryService.getById(Long.valueOf(id)));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid category id provided!");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable String id,
            @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User user) {
        try {
            request.setOwner(user);
            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.setMessage("Category with id #" + id + " updated successfully!");
            response.addContent("category", categoryService.update(request, Long.valueOf(id)));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid category id provided!");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        try {
            categoryService.setOwnerId(user.getId());
            categoryService.deleteById(Long.valueOf(id));
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid category id provided!");
        }
    }
}
