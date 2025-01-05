package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.CategoryRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @PostMapping()
    public ResponseEntity<ApiResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User user) {
        request.setOwner(user);
        var category = categoryService.create(request);
        ApiResponse response = new ApiResponse(true, HttpStatus.CREATED.value());
        response.setMessage("Category created successfully");
        response.addContent("category", category);

        logger.atInfo()
                .addKeyValue("details",
                        Map.of("category_id", category.getId(),
                                "category_name", category.getName()))
                .log("[UUID={}] Category created successfully", user.getUuid());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse> getAllCategories(
            @RequestParam(required = false) boolean namesOnly,
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        categoryService.setOwnerId(user.getId());
        List<?> categories;
        if (namesOnly) {
            categories = categoryService.getAllWithNameOnly(pageable);
        } else {
            categories = categoryService.getAll(pageable);
        }

        ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
        response.setTotal(categories.size());
        response.addContent("categories", categories);

        logger.atInfo()
                .addKeyValue("details",
                        Map.of("categories_total", categories.size()))
                .log("[UUID={}] List of categories retrieved successfully", user.getUuid());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategory(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        try {
            categoryService.setOwnerId(user.getId());

            var category = categoryService.getById(Long.valueOf(id));

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.addContent("category", category);

            logger.atInfo()
                    .addKeyValue("details",
                            Map.of("category_id", category.getId(),
                                    "category_name", category.getName()))
                    .log("[UUID={}] Category retrieved successfully", user.getUuid());

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

            var category = categoryService.update(request, Long.valueOf(id));

            ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
            response.setMessage("Category with id #" + id + " updated successfully!");
            response.addContent("category", category);

            logger.atInfo()
                    .addKeyValue("details",
                            Map.of("category_id", category.getId(),
                                    "category_name", category.getName()))
                    .log("[UUID={}] Category updated successfully", user.getUuid());

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

            logger.atInfo()
                    .addKeyValue("details",
                            Map.of("category_id", id))
                    .log("[UUID={}] Category with all transactions deleted successfully", user.getUuid());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Invalid category id provided!");
        }
    }
}
