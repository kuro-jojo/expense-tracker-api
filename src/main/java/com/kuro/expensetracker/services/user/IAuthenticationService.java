package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.exceptions.EmailConfirmationException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.EmailConfirmationToken;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.UserRequest;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

public interface IAuthenticationService {
    User register(UserRequest request) throws InvalidValueException, MessagingException;

    Map<String, Object> authenticate(UserRequest request) throws BadCredentialsException, EmailConfirmationException;

    User resendConfirmationLink(UserRequest request) throws BadCredentialsException, EmailConfirmationException, MessagingException;

    EmailConfirmationToken generateConfirmationToken(User user);

    boolean confirmEmail(String token) throws EmailConfirmationException;

}
