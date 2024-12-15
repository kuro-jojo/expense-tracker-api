package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.exceptions.UserNotFoundException;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.repositories.UserRepository;
import com.kuro.expensetracker.requests.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;

    @Override
    public User add(UserRequest request) {
        if (request.getName() == null || request.getEmail() == null || request.getPassword() == null) {
            throw new InvalidValueException("Please enter a name, an email and a password");
        }

        if (request.getJoinedAt() == null) {
            request.setJoinedAt(LocalDate.now());
        }

        return userRepository.save(new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getJoinedAt(),
                request.getCurrency()
        ));
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not find!")));
    }

    @Override
    public User update(UserRequest request) {
        return null;
    }

    @Override
    public void deleteUserById(Long id) {

    }
}
