package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.exceptions.AccountNotActivatedException;
import com.kuro.expensetracker.exceptions.ConfirmationEmailException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.OtpRequest;
import com.kuro.expensetracker.requests.UserRequest;
import com.kuro.expensetracker.responses.AuthResponse;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;

public interface IAuthenticationService {
    User register(UserRequest request, boolean byOtp) throws InvalidValueException, MessagingException;

    AuthResponse authenticate(UserRequest request)
            throws BadCredentialsException,
            ConfirmationEmailException,
            MessagingException,
            AccountNotActivatedException;

    User resendConfirmationEmail(UserRequest request, boolean byOTP)
            throws BadCredentialsException, ConfirmationEmailException, MessagingException;

    User resendConfirmationEmail(OtpRequest request)
            throws BadCredentialsException, ConfirmationEmailException, MessagingException;

    void confirmEmail(String token) throws ConfirmationEmailException;

    void verifyOTP(OtpRequest otpRequest) throws ConfirmationEmailException, InvalidValueException;

    List<Long> removeExpiredConfirmationTokens();

    List<Long> removeExpiredOTPs();
}
