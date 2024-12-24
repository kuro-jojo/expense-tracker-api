package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.exceptions.UserNotFoundException;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.repositories.UserRepository;
import com.kuro.expensetracker.requests.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;

    @Override
    public Optional<User> getById(Long id) throws UserNotFoundException{
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
