package com.kuro.expensetracker.services.category;

import com.kuro.expensetracker.exceptions.EntityAlreadyPresentException;
import com.kuro.expensetracker.exceptions.EntityNotFoundException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.Category;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.repositories.CategoryRepository;
import com.kuro.expensetracker.requests.CategoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private final long ownerId = 1L;
    private final long categoryId = 1L;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryService categoryService;
    private Category expectedCategory;

    @BeforeEach
    public void setUp() {
        categoryService.setOwnerId(ownerId);

        expectedCategory = Category.builder()
                .id(categoryId)
                .name("sport")
                .description("For sports transactions")
                .threshold(new BigDecimal(200))
                .transactions(List.of())
                .build();
    }

    @Test
    public void createCategory_withValidRequest_shouldReturnCategory() {
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .owner(new User(ownerId))
                .name("sport")
                .description("For sports transactions")
                .threshold(new BigDecimal(200))
                .build();

        when(categoryRepository.findByNameAndOwnerId(categoryRequest.getName(), ownerId))
                .thenReturn(Optional.empty());

        when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(expectedCategory);

        Category actualCategory = categoryService.create(categoryRequest);
        assertNotNull(actualCategory);
    }

    @Test
    public void createCategory_withInvalidRequest_forNegativeThreshold_shouldThrowInvalidValueException() {
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .owner(new User(ownerId))
                .name("sport")
                .description("For sports transactions")
                .threshold(new BigDecimal(200).negate())
                .build();

        when(categoryRepository.findByNameAndOwnerId(categoryRequest.getName(), ownerId))
                .thenReturn(Optional.empty());

        assertThrowsExactly(InvalidValueException.class, () -> categoryService.create(categoryRequest));
    }

    @Test
    public void createCategory_withExistingCategory_shouldThrowEntityAlreadyPresentException() {
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .owner(new User(ownerId))
                .name("sport")
                .description("For sports transactions")
                .threshold(new BigDecimal(200))
                .build();

        when(categoryRepository.findByNameAndOwnerId(categoryRequest.getName(), ownerId))
                .thenReturn(Optional.of(expectedCategory));

        assertThrowsExactly(EntityAlreadyPresentException.class, () -> categoryService.create(categoryRequest));
    }

    @Test
    public void deleteCategoryById_withValidId_shouldNotThrowException() {

        when(categoryRepository.findByIdAndOwnerId(categoryId, ownerId))
                .thenReturn(Optional.of(expectedCategory));

        assertDoesNotThrow(() -> categoryService.deleteById(categoryId));
    }

    @Test
    public void deleteCategoryById_withInvalidId_shouldThrowEntityNotFoundException() {

        when(categoryRepository.findByIdAndOwnerId(categoryId, ownerId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteById(categoryId));
    }

}