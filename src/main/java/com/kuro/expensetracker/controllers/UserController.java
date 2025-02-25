package com.kuro.expensetracker.controllers;

import com.kuro.expensetracker.models.User;
import com.kuro.expensetracker.requests.UserRequest;
import com.kuro.expensetracker.responses.ApiResponse;
import com.kuro.expensetracker.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse> updateUser(
            @RequestBody UserRequest request,
            @AuthenticationPrincipal User user) {

        if (request.isEmpty()) {
            ApiResponse response = new ApiResponse(false, HttpStatus.BAD_REQUEST.value());
            response.setMessage("Please provide a least one field to update");

            log.atInfo()
                    .log("[UUID={}] Failed to update user profile : No field provided", user.getUuid());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        request.setId(user.getId());
        userService.update(request);

        ApiResponse response = new ApiResponse(true, HttpStatus.OK.value());
        response.setMessage("User profile updated successfully");

        log.atInfo()
                .log("[UUID={}] User profile updated successfully", user.getUuid());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<ApiResponse> deleteUser(
            @AuthenticationPrincipal User user) {
        userService.delete(user);

        log.atInfo()
                .log("[UUID={}] User account deleted successfully", user.getUuid());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
