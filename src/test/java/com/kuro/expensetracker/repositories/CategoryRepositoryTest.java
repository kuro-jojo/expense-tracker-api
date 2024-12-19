package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;
    private Category testCategory;

    @BeforeEach
    public void setUp() {
        // Initialize test data before each test method
        testCategory = Category.builder()
                .name("sport")
                .description("For sports transactions")
                .threshold(203.21f)
                .build();
        categoryRepository.save(testCategory);
    }

    @AfterEach
    public void tearDown() {
        // Release test data after each test method
        categoryRepository.delete(testCategory);
    }

    @Test
    public void CategoryRepository_SaveAll_ReturnSavedCategory() {
        // Act
        Category savedCategory = categoryRepository.save(testCategory);
        // Assert
        assertNotNull(savedCategory);
        assertTrue(savedCategory.getId() > 0);
        assertEquals(savedCategory.getName(), testCategory.getName());
    }

    @Test
    void CategoryRepository_FindByName_ReturnTrue() {
        Optional<Category> foundCategory = categoryRepository.findByName("sport");
        assertTrue(foundCategory.isPresent());
        assertEquals(testCategory.getName(), foundCategory.get().getName());
    }

    @Test
    void CategoryRepository_FindByName_ReturnFalse() {
        Optional<Category> foundCategory = categoryRepository.findByName("");
        assertTrue(foundCategory.isEmpty());
    }
}