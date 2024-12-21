package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.auth.JwtService;
import com.kuro.expensetracker.utils.PasswordValidator;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.repositories.UserRepository;
import com.kuro.expensetracker.requests.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Currency;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User register(UserRequest request) {
        if (request.getName() == null) {
            throw new InvalidValueException("Must provide a name.");
        }

        if (!PasswordValidator.isPasswordValid(request.getPassword())) {
            throw new InvalidValueException("Password must match : " + PasswordValidator.PASSWORD_REQUIREMENT);
        }

        return userRepository.save(new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                LocalDate.now(),
                Currency.getInstance(Locale.FRANCE) // TODO : update this with the actual user locality
        ));
    }

    public String authenticate(UserRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword())
        );
        return jwtService.generateToken(
                userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new BadCredentialsException("Invalid email or password!")));
    }
}
