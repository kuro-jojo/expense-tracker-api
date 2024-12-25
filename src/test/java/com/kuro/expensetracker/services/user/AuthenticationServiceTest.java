package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.auth.JwtService;
import com.kuro.expensetracker.exceptions.EmailConfirmationException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.EmailConfirmationToken;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.repositories.EmailConfirmationTokenRepository;
import com.kuro.expensetracker.repositories.UserRepository;
import com.kuro.expensetracker.requests.UserRequest;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private final String correctPassword = "Password1@";
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private AuthenticationService authenticationService;
    private User expectedUser;

    @BeforeEach
    public void setUp() {
        expectedUser = User.builder()
                .uuid(String.valueOf(UUID.randomUUID()))
                .name("Kuro")
                .email("kuro@test.com")
                .password(passwordEncoder.encode(correctPassword))
                .currency(Currency.getInstance(Locale.FRANCE))
                .joinedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void registerUser_withValidRequest_shouldReturnUser() {
        UserRequest request = UserRequest.builder()
                .name("Kuro")
                .email("kuro@test.com")
                .password(correctPassword)
                .build();

        EmailConfirmationToken emailConfirmationToken = Mockito.mock(EmailConfirmationToken.class);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        when(emailConfirmationTokenRepository.save(any(EmailConfirmationToken.class))).thenReturn(
                emailConfirmationToken
        );


        var savedUser = userRepository.save(expectedUser);
        emailConfirmationToken = authenticationService.generateConfirmationToken(savedUser);
        try {
            doNothing().when(emailService).sendConfirmationEmail(emailConfirmationToken);
            var actualUser = authenticationService.register(request);
            Assertions.assertNotNull(actualUser);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void registerUser_withInValidNameInRequest_shouldThrowInvalidValueException() {
        UserRequest request = UserRequest.builder()
                .email("kuro@test.com")
                .password(correctPassword)
                .build();

        Assertions.assertThrows(InvalidValueException.class, () -> authenticationService.register(request));
    }

    @Test
    public void registerUser_withInValidPasswordInRequest_shouldThrowInvalidValueException() {
        UserRequest request = UserRequest.builder()
                .name("Kuro")
                .email("kuro@test.com")
                .password("correctPassword")
                .build();

        Assertions.assertThrows(InvalidValueException.class, () -> authenticationService.register(request));
    }

    @Test
    public void authenticate_withValidRequest_shouldReturnTokenString() {
        Authentication authentication = mock(Authentication.class);
        UserRequest request = UserRequest.builder()
                .email("kuro@test.com")
                .password(correctPassword)
                .build();
        var expectedToken = "jwt token";

        expectedUser.setIsVerified(true);

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(expectedUser));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtService.generateToken(any(User.class))).thenReturn(expectedToken);

        String tokenString = authenticationService.authenticate(request);
        assertNotNull(tokenString);
        assertEquals(expectedToken, tokenString);
    }

    @Test
    public void authenticate_withInvalidEmail_shouldThrowBadCredentialsException() {
        UserRequest request = UserRequest.builder()
                .email("invalid@test.com")
                .password(correctPassword)
                .build();

        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    public void authenticate_withInvalidPassword_shouldThrowBadCredentialsException() {
        UserRequest request = UserRequest.builder()
                .email("kuro@test.com")
                .password(correctPassword)
                .build();

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(expectedUser));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    public void authenticate_withUnVerifiedUser_shouldThrowEmailConfirmationException() {
        Authentication authentication = mock(Authentication.class);
        UserRequest request = UserRequest.builder()
                .email("kuro@test.com")
                .password(correctPassword)
                .build();

        expectedUser.setIsVerified(false);

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(expectedUser));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        assertThrows(EmailConfirmationException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    public void confirmEmail_withValidToken_shouldReturnTrue() {
        var token = "secret-token";
        var emailConfirmationToken = EmailConfirmationToken.builder()
                .user(expectedUser)
                .token(token)
                .expiration(LocalDateTime.now().plusHours(2L))
                .build();

        when(emailConfirmationTokenRepository.findByToken(token)).thenReturn(Optional.of(emailConfirmationToken));
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        doNothing().when(emailConfirmationTokenRepository).delete(emailConfirmationToken);

        assertTrue(authenticationService.confirmEmail(token));
    }

    @Test
    public void confirmEmail_withInvalidUserInToken_shouldReturnFalse() {
        var token = "secret-token";
        var emailConfirmationToken = EmailConfirmationToken.builder()
                .token(token)
                .expiration(LocalDateTime.now())
                .build();

        when(emailConfirmationTokenRepository.findByToken(token)).thenReturn(Optional.of(emailConfirmationToken));
        assertFalse(authenticationService.confirmEmail(token));
    }

    @Test
    public void confirmEmail_withExpiredToken_shouldReturnFalse() {
        var token = "secret-token";
        var emailConfirmationToken = EmailConfirmationToken.builder()
                .user(expectedUser)
                .token(token)
                .expiration(LocalDateTime.now())
                .build();

        when(emailConfirmationTokenRepository.findByToken(token)).thenReturn(Optional.of(emailConfirmationToken));

        assertFalse(authenticationService.confirmEmail(token));
    }

    @Test
    public void confirmEmail_withUnknownToken_shouldReturnFalse() {
        var token = "secret-token";

        assertFalse(authenticationService.confirmEmail(token));
    }
}