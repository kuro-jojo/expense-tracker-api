package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.exceptions.EmailConfirmationException;
import com.kuro.expensetracker.exceptions.EntityAlreadyPresentException;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.UserRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.user.AuthenticationService;
import com.kuro.expensetracker.services.user.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserRequest request) throws EmailConfirmationException {
        String token = authenticationService.authenticate(request);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Token ", token));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRequest request) throws MessagingException {
        try {
            var user = authenticationService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("User registered successfully. Email sent to " + user.getEmail(), user));
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyPresentException(User.class, request.getEmail());
        }
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(
            @RequestParam String token) throws EmailConfirmationException {
        if (authenticationService.confirmEmail(token)) {
            return ResponseEntity.ok("Your email has been successfully verified.");
        }
        return ResponseEntity.ok("User details not found or Link expired. Please login and regenerate the confirmation link.");
    }
}
