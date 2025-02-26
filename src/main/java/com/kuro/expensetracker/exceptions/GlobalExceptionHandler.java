package com.kuro.expensetracker.exceptions;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.SignatureException;
import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException exception) {
        log.error("[BadCredentialsException] Authentication failed: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed");
        errorDetail.setProperty("message", "Bad credentials");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ProblemDetail handleJwtAuthenticationException(JwtAuthenticationException exception) {
        log.error("[JwtAuthenticationProvider] Authentication failed: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed");
        errorDetail.setProperty("message", "Invalid authentication token");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(JwtException.class)
    public ProblemDetail handleJwtExceptions(JwtException exception) {
        log.error("[JwtException - {}] Authentication failed: {}",
                exception.getClass().getSimpleName(),
                exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed");
        errorDetail.setProperty("message", "Invalid authentication token");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(SignatureException.class)
    public ProblemDetail handleSignatureException(SignatureException exception) {
        log.error("[SignatureException] Authentication failed: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed");
        errorDetail.setProperty("message", "Invalid authentication token");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }


    @ExceptionHandler(AccountAlreadyActivatedException.class)
    public String handleAccountAlreadyActivatedException(AccountAlreadyActivatedException exception) {
        log.error("[AccountAlreadyActivatedException] User [{}] account already activated", exception.getUuid());
        return exception.getMessage();
    }

    @ExceptionHandler(ConfirmationEmailException.class)
    public ProblemDetail handleConfirmationEmailException(ConfirmationEmailException exception) {
        log.error("[ConfirmationEmailException] Failed to confirm email: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Email verification is pending or could not be processed");
        errorDetail.setProperty("message", exception.getMessage());
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("[MethodArgumentNotValidException] Constraint violated: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid request parameter");
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            log.error("[MethodArgumentNotValidException] Incorrect {} name : {}", fieldName, errorMessage);
            errorDetail.setProperty(fieldName, errorMessage);
        });
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(InvalidValueException.class)
    public ProblemDetail handleInvalidValueException(InvalidValueException exception) {
        log.error("[InvalidValueException] : {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid value");
        errorDetail.setProperty("message", exception.getMessage());
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleRequestBodyMissingException(HttpMessageNotReadableException exception) {
        log.error("[HttpMessageNotReadableException] {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        if (exception.getMessage().contains("body is missing")) {
            errorDetail.setProperty("message", "Request body is missing");
        } else if (exception.getMessage().contains("JSON parse error")) {
            errorDetail.setDetail("Invalid input");
            errorDetail.setProperty("message", "One or more fields have invalid values. Please check your request.");
        } else {
            throw new RuntimeException(exception);
        }
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error("[MethodArgumentTypeMismatchException] {}", exception.getMessage());
        String paramName = exception.getName();
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid value for parameter '" + paramName + "'.");
        errorDetail.setProperty("message", exception.getMessage());
        errorDetail.setProperty("timestamp", Instant.now());

        return errorDetail;
    }


    @ExceptionHandler(EntityAlreadyPresentException.class)
    public ProblemDetail handleEntityAlreadyPresentException(EntityAlreadyPresentException exception) {
        log.error("[{}] {}", exception.getClass().getSimpleName(), exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        errorDetail.setProperty("message", exception.getMessage());
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException exception) {
        log.error("[EntityNotFoundException] {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        errorDetail.setProperty("message", exception.getMessage());
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFoundException(NoResourceFoundException exception) {
        log.error("[NoResourceFoundException] {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        errorDetail.setProperty("message", String.format("Resource %s not found", exception.getResourcePath()));
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error("Got a data integrity violation exception: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error");
        errorDetail.setProperty("message", "An unexpected error occurred");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(MailException.class)
    public ProblemDetail handleMailException(MailException exception) {
        log.error("[{}] Exception with the mail server: {}",
                exception.getClass().getSimpleName(),
                exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error");
        errorDetail.setProperty("message", "An unexpected error occurred with mail server");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleResourceAccessException(ResourceAccessException exception) {
        log.error("[{}] {}", exception.getClass().getSimpleName(), exception.getMessage());
        if (exception.getMessage().contains("Connection refused")) {
            ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_GATEWAY,
                    "Service unavailable");
            errorDetail.setProperty(
                    "message",
                    "The service is temporarily unavailable due to an upstream dependency. Please try again later.");
            errorDetail.setProperty("timestamp", Instant.now());
            return errorDetail;
        }
        throw new RuntimeException(exception);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception exception) {
        log.error("[{}] Unhandled exception: {}",
                exception.getClass().getSimpleName(),
                exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error");
        errorDetail.setProperty("message", "An unexpected error occurred");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }
}
