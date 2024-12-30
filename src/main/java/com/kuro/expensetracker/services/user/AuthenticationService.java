package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.auth.JwtService;
import com.kuro.expensetracker.exceptions.AccountAlreadyActivatedException;
import com.kuro.expensetracker.exceptions.EmailConfirmationException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.EmailConfirmationToken;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.repositories.EmailConfirmationTokenRepository;
import com.kuro.expensetracker.repositories.UserRepository;
import com.kuro.expensetracker.requests.UserRequest;
import com.kuro.expensetracker.utils.PasswordValidator;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    private final UserRepository userRepository;
    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Value("${security.email-confirmation.expiration-time}")
    private long emailConfirmationExpiration;

    @Override
    @Transactional
    public User register(UserRequest request) throws InvalidValueException, MessagingException {
        if (request.getName() == null) {
            throw new InvalidValueException("Must provide a name.");
        }

        if (!PasswordValidator.isPasswordValid(request.getPassword())) {
            throw new InvalidValueException("Password must match : " + PasswordValidator.PASSWORD_REQUIREMENT);
        }

        var user = userRepository.save(new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                LocalDateTime.now(),
                Currency.getInstance(Locale.FRANCE) // TODO : update this with the actual user locality
        ));
        // Generate a token for confirmation
        var emailConfirmationToken = generateConfirmationToken(user);
        emailService.sendConfirmationEmail(emailConfirmationToken);
        return user;
    }

    @Override
    public Map<String, Object> authenticate(UserRequest request) throws BadCredentialsException, EmailConfirmationException {
        var user = getUserToAuthenticate(request);

        if (!user.getIsVerified()) {
            throw new EmailConfirmationException("The email need to be confirmed");
        }

        return Map.of("token", jwtService.generateToken(user), "user", user);
    }

    @Override
    public User resendConfirmationLink(UserRequest request) throws BadCredentialsException, EmailConfirmationException, MessagingException {
        var user = getUserToAuthenticate(request);

        if (user.getIsVerified()) {
            throw new AccountAlreadyActivatedException();
        }

        emailConfirmationTokenRepository.findByUserId(user.getId()).ifPresent(emailConfirmationTokenRepository::delete);

        var emailConfirmationToken = generateConfirmationToken(user);
        emailService.sendConfirmationEmail(emailConfirmationToken);
        return user;
    }

    private User getUserToAuthenticate(UserRequest request) throws BadCredentialsException, EmailConfirmationException {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException(String.format("User with email [%s] not found.", request.getEmail())));

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword())
        );

        if (auth == null) {
            throw new BadCredentialsException("Cannot authenticate the user with email [" + user.getEmail() + "]");
        }

        return user;
    }

    @Override
    public EmailConfirmationToken generateConfirmationToken(User user) {
        SecureRandom secureRandom = new SecureRandom();

        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String tokenValue = new String(Base64.getUrlEncoder().encode(randomBytes));

        EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken();
        emailConfirmationToken.setToken(tokenValue);
        emailConfirmationToken.setExpiration(LocalDateTime.now().plusHours(emailConfirmationExpiration));
        emailConfirmationToken.setUser(user);
        return emailConfirmationTokenRepository.save(emailConfirmationToken);
    }

    @Override
    @Transactional
    public boolean confirmEmail(String token) throws EmailConfirmationException {
        var emailConfirmationTokenOpt = emailConfirmationTokenRepository.findByToken(token);
        if (emailConfirmationTokenOpt.isEmpty()) {
            return false;
        }

        var emailConfirmationToken = emailConfirmationTokenOpt.get();
        var user = emailConfirmationToken.getUser();
        if (user == null) {
            emailConfirmationTokenRepository.delete(emailConfirmationToken);
            return false;
        }

        if (!emailConfirmationToken.isValid()) {
            // remove the token and the user
            emailConfirmationTokenRepository.delete(emailConfirmationToken);
            userRepository.delete(user);
            return false;
        }

        user.setIsVerified(true);
        userRepository.save(user);
        emailConfirmationTokenRepository.delete(emailConfirmationToken);
        return true;
    }
}
