package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.exceptions.InvalidValueException;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Category added successfully", categoryService.create(request)));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse> getAllCategories(@AuthenticationPrincipal User user) {
        categoryService.setOwnerId(user.getId());
        var categories = categoryService.getAll();
        return ResponseEntity.ok(new ApiResponse(categories, categories.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategory(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        try {
            categoryService.setOwnerId(user.getId());
            return ResponseEntity.ok(new ApiResponse(categoryService.getById(Long.valueOf(id))));
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
            return ResponseEntity.ok(new ApiResponse("Category with id #" + id + " updated successfully!", categoryService.update(request, Long.valueOf(id))));
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
