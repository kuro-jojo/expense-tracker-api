package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.auth.JwtService;
import com.kuro.expensetracker.exceptions.AccountAlreadyActivatedException;
import com.kuro.expensetracker.exceptions.ConfirmationEmailException;
import com.kuro.expensetracker.exceptions.InvalidValueException;
import com.kuro.expensetracker.models.ConfirmationEmailToken;
import com.kuro.expensetracker.models.OTP;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.repositories.ConfirmationEmailTokenRepository;
import com.kuro.expensetracker.repositories.OTPRepository;
import com.kuro.expensetracker.repositories.UserRepository;
import com.kuro.expensetracker.requests.UserRequest;
import com.kuro.expensetracker.requests.VerifyOtpRequest;
import com.kuro.expensetracker.responses.AuthResponse;
import com.kuro.expensetracker.utils.EmailValidator;
import com.kuro.expensetracker.utils.PasswordValidator;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements IAuthenticationService {
    private final UserRepository userRepository;
    private final ConfirmationEmailTokenRepository confirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final OTPRepository otpRepository;

    @Value("${security.email-confirmation.expiration-time}")
    private long emailConfirmationExpiration;
    @Value("${security.email-confirmation.otp.length:6}")
    private int otpLength;
    @Value("${security.email-confirmation.otp.expiration:10}")
    private long otpExpiration;

    @Override
    @Transactional
    public User register(UserRequest request, boolean byOTP)
            throws InvalidValueException, MessagingException {
        if (request.getName() == null) {
            throw new InvalidValueException("Must provide a name.");
        }

        if (EmailValidator.isEmailInvalid(request.getEmail())) {
            throw new InvalidValueException("Must be a well-formed email address.");
        }

        if (PasswordValidator.isPasswordInvalid(request.getPassword())) {
            throw new InvalidValueException(
                    "Password must match : " + PasswordValidator.PASSWORD_REQUIREMENT);
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .joinedAt(LocalDateTime.now())
                .isVerified(false)
                .currency(Currency.getInstance(Locale.FRANCE)) // TODO : update this with the actual user locality
                .build();

        user = userRepository.save(user);
        // Generate a token or an OTP for the confirmation email
        if (byOTP) {
            var otp = generateOTP(user);
            emailService.sendConfirmationEmail(otp, user);
            log.info("OTP sent by mail to {}.", user.getEmail());

            user.setOtp(otp);
        } else {
            var emailConfirmationToken = generateConfirmationToken(user);
            emailService.sendConfirmationEmail(emailConfirmationToken);
            log.info("Confirmation email sent to {}.", user.getEmail());
        }
        return user;
    }

    @Override
    public AuthResponse authenticate(UserRequest request)
            throws BadCredentialsException, ConfirmationEmailException {
        var user = getUserToAuthenticate(request);

        if (!user.getIsVerified()) {
            throw new ConfirmationEmailException("Your account is not activated yet. " +
                    "Please verify your email to continue or Request a new confirmation email.");
        }
        return new AuthResponse(jwtService.generateToken(user), user);
    }

    @Override
    public User resendConfirmationEmail(UserRequest request, boolean byOTP)
            throws BadCredentialsException, ConfirmationEmailException, MessagingException {
        var user = getUserToAuthenticate(request);

        if (user.getIsVerified()) {
            throw new AccountAlreadyActivatedException(user.getUuid());
        }

        if (byOTP) {
            otpRepository.findByEmail(user.getEmail())
                    .ifPresent(otpRepository::delete);

            var otp = generateOTP(user);
            emailService.sendConfirmationEmail(otp, user);
            user.setOtp(otp);
        } else {
            handleInvalidToken(user);
            var emailConfirmationToken = generateConfirmationToken(user);
            emailService.sendConfirmationEmail(emailConfirmationToken);
        }
        return user;
    }


    @Override
    public void confirmEmail(String token) throws ConfirmationEmailException {
        var emailConfirmationTokenOpt = confirmationTokenRepository.findByToken(token);
        var exceptionMessage = "User details not found or the link has expired. " +
                "If you already registered, please request a new confirmation email.";

        if (emailConfirmationTokenOpt.isEmpty()) {
            log.info("Confirm Email : Confirmation email token not found!");
            throw new ConfirmationEmailException(exceptionMessage);
        }

        var confirmationEmailToken = emailConfirmationTokenOpt.get();
        var user = confirmationEmailToken.getUser();
        if (user == null) {
            confirmationTokenRepository.delete(confirmationEmailToken);
            log.info("Confirm Email : Removing confirmation email token - User not found!");
            throw new ConfirmationEmailException(exceptionMessage);
        }

        if (!confirmationEmailToken.isValid()) {
            handleInvalidToken(user);
            log.info("Confirm Email : Removing confirmation email token - Token expired!");
            throw new ConfirmationEmailException(exceptionMessage);
        }

        user.setIsVerified(true);
        handleInvalidToken(user);
    }

    @Override
    public void verifyOTP(VerifyOtpRequest otpRequest)
            throws ConfirmationEmailException, InvalidValueException {
        Optional<OTP> otpOpt;
        if (otpRequest.sessionID() != null) {
            otpOpt = otpRepository.findBySessionIDAndOtp(otpRequest.sessionID(), otpRequest.otp());
        } else {
            throw new InvalidValueException("Please provide the OTP and the sessionID");
        }

        var exceptionMessage = "Invalid OTP or expired OTP. " +
                "If you already registered, please request a OTP.";

        if (otpOpt.isEmpty()) {
            log.info("Verify OTP : OTP not found!");
            throw new ConfirmationEmailException(exceptionMessage);
        }

        var otp = otpOpt.get();
        var userOpt = userRepository.findByEmail(otp.getEmail());
        if (userOpt.isEmpty()) {
            otpRepository.delete(otp);
            log.info("Confirm Email : Removing OTP - User not found!");
            throw new ConfirmationEmailException(exceptionMessage);
        }

        if (!otp.isValid()) {
            // remove the otp only
            otpRepository.delete(otp);
            log.info("Confirm Email : Removing OTP - OTP expired!");
            throw new ConfirmationEmailException(exceptionMessage);
        }

        userOpt.get().setIsVerified(true);
        otpRepository.delete(otp);
        userRepository.save(userOpt.get());
    }

    @Override
    @Transactional
    public List<Long> removeExpiredConfirmationTokens() {
        var confirmationEmailTokens = confirmationTokenRepository.findExpiredTokens();
        confirmationTokenRepository.deleteAll(confirmationEmailTokens);
        return confirmationEmailTokens.stream().map(ConfirmationEmailToken::getId).toList();
    }

    @Override
    @Transactional
    public List<Long> removeExpiredOTPs() {
        var otps = otpRepository.findExpiredOTPs();
        otpRepository.deleteAll(otps);
        return otps.stream().map(OTP::getId).toList();
    }

    private void handleInvalidToken(User user) {
        user.setConfirmationEmailToken(null);
        userRepository.save(user);
    }

    private String generateSessionID() {
        SecureRandom secureRandom = new SecureRandom();

        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return new String(Base64.getUrlEncoder().encode(randomBytes));
    }

    private ConfirmationEmailToken generateConfirmationToken(User user) {
        SecureRandom secureRandom = new SecureRandom();

        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String tokenValue = new String(Base64.getUrlEncoder().encode(randomBytes));

        var emailConfirmationToken = ConfirmationEmailToken.builder()
                .token(tokenValue)
                .expiration(LocalDateTime.now().plusHours(emailConfirmationExpiration))
                .user(user)
                .build();
        return confirmationTokenRepository.save(emailConfirmationToken);
    }

    private OTP generateOTP(User user) {
        StringBuilder otpString = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < otpLength; i++) {
            otpString.append(secureRandom.nextInt(10));
        }
        var otp = OTP.builder()
                .otp(otpString.toString())
                .expiration(LocalDateTime.now().plusMinutes(otpExpiration))
                .sessionID(generateSessionID())
                .email(user.getEmail())
                .build();

        return otpRepository.save(otp);
    }

    private User getUserToAuthenticate(UserRequest request)
            throws BadCredentialsException, ConfirmationEmailException {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                String.format("User with email [%s] not found.", request.getEmail())));

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword())
        );

        if (auth == null) {
            throw new BadCredentialsException("Cannot authenticate the user with email [" + user.getEmail() + "]");
        }

        return user;
    }
}
