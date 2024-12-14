package com.kuro.expensetracker.repositories;

import com.kuro.expensetracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long>{
}
