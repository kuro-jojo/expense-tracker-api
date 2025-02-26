package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.exceptions.UserNotFoundException;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.UserRequest;
import jakarta.mail.MessagingException;

import java.util.Optional;

public interface IUserService {
    Optional<User> getById(Long id) throws UserNotFoundException;

    void update(UserRequest user);

    void delete(User user) throws MessagingException;
}
