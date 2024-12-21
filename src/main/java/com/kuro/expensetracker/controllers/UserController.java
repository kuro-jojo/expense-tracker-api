package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.exceptions.EntityAlreadyPresentException;
import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.UserRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.user.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserRequest request) {
        String token = authenticationService.authenticate(request);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Token ", token));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRequest request) {
        try {
            var user = authenticationService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("User registered successfully", user));
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyPresentException(User.class, request.getEmail());
        }
    }
}
