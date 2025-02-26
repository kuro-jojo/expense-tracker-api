package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.exceptions.ConfirmationEmailException;
import com.kuro.expensetracker.exceptions.UserAlreadyPresentException;
import com.kuro.expensetracker.requests.UserRequest;
import com.kuro.expensetracker.requests.VerifyOtpRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.responses.AuthResponse;
import com.kuro.expensetracker.services.user.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserRequest request) throws ConfirmationEmailException {
        AuthResponse authenticatedUser = authenticationService.authenticate(request);
        ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
        response.addContent("token", authenticatedUser.token());

        log.atInfo()
                .log("[UUID={}] User logged successfully", (authenticatedUser.user()).getUuid());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody UserRequest request,
            @RequestParam(required = false, defaultValue = "true") boolean byOTP
    ) throws MessagingException {
        try {
            var user = authenticationService.register(request, byOTP);

            ApiResponse response = new ApiResponse(true, HttpStatus.CREATED.value());
            response.setMessage("User registered successfully. Email sent to " + user.getEmail());
            response.addContent("user", user);

            log.atInfo()
                    .log("[UUID={}] User registered successfully", user.getUuid());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new UserAlreadyPresentException(request.getEmail());
            }
            throw e;
        }
    }

    @PostMapping("/resend-confirmation-email")
    public ResponseEntity<ApiResponse> resendConfirmationEmail(
            @Valid @RequestBody UserRequest request,
            @RequestParam(required = false, defaultValue = "true") boolean byOTP
    ) throws ConfirmationEmailException, MessagingException {
        var user = authenticationService.resendConfirmationEmail(request, byOTP);

        ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
        response.setMessage("Email resent to " + request.getEmail());

        log.atInfo()
                .log("[UUID={}] Confirmation link resent to the user successfully", user.getUuid());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestParam String token)
            throws ConfirmationEmailException {
        authenticationService.confirmEmail(token);

        log.atInfo()
                .log("User email verified successfully");

        return ResponseEntity.ok("Your email has been successfully verified.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestBody VerifyOtpRequest otpRequest)
            throws ConfirmationEmailException {
        authenticationService.verifyOTP(otpRequest);
        log.atInfo()
                .log("User email verified successfully");

        return ResponseEntity.ok("Your email has been successfully verified.");
    }
}
