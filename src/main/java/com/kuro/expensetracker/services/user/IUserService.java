package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.UserRequest;

import java.util.Optional;

public interface IUserService {
    User add(UserRequest user);
    Optional<User> getById(Long id);
    User update(UserRequest user);
    void deleteUserById(Long id);
}
