package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.models.User;

import java.util.Optional;

public interface IUserService {
    User add(User user);
    Optional<User> getById(Long id);
    User update(User user);
    void deleteUserById(Long id);
}
