package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<Category> findByName(String name);

    Optional<Category> findByNameAndOwnerId(String name, Long ownerId);

    List<Category> findByOwnerId(Long ownerId);
}
