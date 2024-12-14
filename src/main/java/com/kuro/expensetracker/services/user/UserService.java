package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.exceptions.UserNotFoundException;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    @Override
    public User add(User user) {
        return null;
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not find!")));
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void deleteUserById(Long id) {

    }
}
