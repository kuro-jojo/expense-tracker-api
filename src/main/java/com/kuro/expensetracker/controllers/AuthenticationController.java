package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.exceptions.EmailConfirmationException;
import com.kuro.expensetracker.exceptions.UserAlreadyPresentException;
import com.kuro.expensetracker.requests.UserRequest;
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
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserRequest request) throws EmailConfirmationException {
        AuthResponse authenticatedUser = authenticationService.authenticate(request);
        ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
        response.addContent("token", authenticatedUser.token());

        log.atInfo()
                .log("[UUID={}] User logged successfully", (authenticatedUser.user()).getUuid());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRequest request) throws MessagingException {
        try {
            var user = authenticationService.register(request);

            ApiResponse response = new ApiResponse(true, HttpStatus.CREATED.value());
            response.setMessage("User registered successfully. Email sent to " + user.getEmail());
            response.addContent("user", user);

            log.atInfo()
                    .log("[UUID={}] User registered successfully", user.getUuid());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyPresentException(request.getEmail());
        }
    }

    @PostMapping("/resend-confirmation-link")
    public ResponseEntity<ApiResponse> resendConfirmationLink(@Valid @RequestBody UserRequest request) throws EmailConfirmationException, MessagingException {
        var user = authenticationService.resendConfirmationLink(request);

        ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
        response.setMessage("Email resent to " + request.getEmail());

        log.atInfo()
                .log("[UUID={}] Confirmation link resent to the user successfully", user.getUuid());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(
            @RequestParam String token) throws EmailConfirmationException {
        if (authenticationService.confirmEmail(token)) {
            log.atInfo()
                    .log("User email verified successfully");

            return ResponseEntity.ok("Your email has been successfully verified.");
        }
        log.atInfo()
                .log("Failed to verified user email : User details not found or the link has expired");

        return ResponseEntity.ok("User details not found or the link has expired. If you already registered, please request a new confirmation link.");
    }

}
