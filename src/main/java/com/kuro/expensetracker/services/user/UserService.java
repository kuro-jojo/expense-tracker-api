package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.exceptions.UserNotFoundException;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.repositories.UserRepository;
import com.kuro.expensetracker.requests.UserRequest;
import com.kuro.expensetracker.utils.PasswordValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getById(Long id) throws UserNotFoundException {
        return Optional.ofNullable(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not find!")));
    }

    @Override
    @Transactional
    public void update(UserRequest request) {

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (!PasswordValidator.isPasswordValid(request.getPassword())) {
                throw new InvalidValueException("Password must match : " + PasswordValidator.PASSWORD_REQUIREMENT);
            }
            var password = passwordEncoder.encode(request.getPassword());
            userRepository.updatePassword(request.getId(), password);
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            userRepository.updateName(request.getId(), request.getName());
        }
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }
}
