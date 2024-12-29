package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.exceptions.EmailConfirmationException;
import com.kuro.expensetracker.exceptions.UserAlreadyPresentException;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.UserRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.user.AuthenticationService;
import com.kuro.expensetracker.services.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserRequest request) throws EmailConfirmationException {
        String token = authenticationService.authenticate(request);
        ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
        response.addContent("token", token);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRequest request) throws MessagingException {
        try {
            var user = authenticationService.register(request);

            ApiResponse response = new ApiResponse(true, HttpStatus.CREATED.value());
            response.setMessage("User registered successfully. Email sent to " + user.getEmail());
            response.addContent("user", user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyPresentException(request.getEmail());
        }
    }

    @PostMapping("/resend-confirmation-link")
    public ResponseEntity<ApiResponse> resendConfirmationLink(@Valid @RequestBody UserRequest request) throws EmailConfirmationException, MessagingException {
        authenticationService.resendConfirmationLink(request);

        ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
        response.setMessage("Email resent to " + request.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(
            @RequestParam String token) throws EmailConfirmationException {
        if (authenticationService.confirmEmail(token)) {
            return ResponseEntity.ok("Your email has been successfully verified.");
        }
        return ResponseEntity.ok("User details not found or the link has expired. If you already registered, please request a new confirmation link.");
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse> updateUser(
            @RequestBody UserRequest request,
            @AuthenticationPrincipal User user) {

        if (request.isEmpty()) {
            ApiResponse response = new ApiResponse(false, HttpStatus.BAD_REQUEST.value());
            response.setMessage("Please provide a least one field to update");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        request.setId(user.getId());
        userService.update(request);

        ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
        response.setMessage("User updated successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<ApiResponse> deleteUser(
            @AuthenticationPrincipal User user) {
        userService.delete(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
