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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserRequest request) throws EmailConfirmationException {
        Map<String, Object> authenticatedUser = authenticationService.authenticate(request);
        ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
        response.addContent("token", authenticatedUser.get("token"));

        logger.atInfo()
                .log("[UUID={}] User logged successfully", ((User) authenticatedUser.get("user")).getUuid());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRequest request) throws MessagingException {
        try {
            var user = authenticationService.register(request);

            ApiResponse response = new ApiResponse(true, HttpStatus.CREATED.value());
            response.setMessage("User registered successfully. Email sent to " + user.getEmail());
            response.addContent("user", user);

            logger.atInfo()
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

        logger.atInfo()
                .log("[UUID={}] Confirmation link resent to the user successfully", user.getUuid());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(
            @RequestParam String token) throws EmailConfirmationException {
        if (authenticationService.confirmEmail(token)) {
            logger.atInfo()
                    .log("User email verified successfully");

            return ResponseEntity.ok("Your email has been successfully verified.");
        }
        logger.atInfo()
                .log("Failed to verified user email : User details not found or the link has expired");

        return ResponseEntity.ok("User details not found or the link has expired. If you already registered, please request a new confirmation link.");
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse> updateUser(
            @RequestBody UserRequest request,
            @AuthenticationPrincipal User user) {

        if (request.isEmpty()) {
            ApiResponse response = new ApiResponse(false, HttpStatus.BAD_REQUEST.value());
            response.setMessage("Please provide a least one field to update");

            logger.atInfo()
                    .log("[UUID={}] Failed to update user profile : No field provided", user.getUuid());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        request.setId(user.getId());
        userService.update(request);

        ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
        response.setMessage("User profile updated successfully");

        logger.atInfo()
                .log("[UUID={}] User profile updated successfully", user.getUuid());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<ApiResponse> deleteUser(
            @AuthenticationPrincipal User user) {
        userService.delete(user);

        logger.atInfo()
                .log("[UUID={}] User account deleted successfully", user.getUuid());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
