package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.exceptions.EntityAlreadyPresentException;
import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.requests.CategoryRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping()
    public ResponseEntity<ApiResponse> addCategory(@RequestBody CategoryRequest request) {
        try {
            var category = categoryService.add(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Category added successfully", category));
        } catch (EntityAlreadyPresentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage()));
        } catch (InvalidValueException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage()));
        }
    }

    @GetMapping()
    public ResponseEntity<ApiResponse> getAllCategories() {
        // TODO : handle authentication
        return ResponseEntity.ok(new ApiResponse("Categories", categoryService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategory(@PathVariable String id) {
        try {
            return categoryService.getById(Long.valueOf(id)).map(
                    category -> ResponseEntity.ok(new ApiResponse("Category found!", category))
            ).orElse(
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Category with id #" + id + " not found!"))
            );
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Invalid category id provided!"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable String id) {
        try {
            categoryService.deleteById(Long.valueOf(id));
            return ResponseEntity.ok(new ApiResponse("Category with id #" + id + " deleted successfully!"));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Invalid category id provided!"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Category with id #" + id + " not found!"));
        }
    }

}
