package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUuid(String uuid);

    Optional<User> findByEmailAndPassword(String email, String password);

    @Modifying
    @Query("update User u set u.name = :name where u.id = :id")
    void updateName(@Param(value = "id") Long id, @Param(value = "name") String name);

    @Modifying
    @Query("update User u set password = :password where u.id = :id")
    void updatePassword(@Param(value = "id") Long id, @Param(value = "password") String password);
}
