package com.kuro.expensetracker.exceptions;

import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException exception) {
        logger.error("[BadCredentialsException] Authentication failed: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed");
        errorDetail.setProperty("message", "Bad credentials");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ProblemDetail handleJwtAuthenticationException(JwtAuthenticationException exception) {
        logger.error("[JwtAuthenticationProvider] Authentication failed: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed");
        errorDetail.setProperty("message", "Invalid authentication token");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(JwtException.class)
    public ProblemDetail handleJwtExceptions(JwtException exception) {
        logger.error("[JwtException - {}] Authentication failed : {}", exception.getClass().getSimpleName(), exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed");
        errorDetail.setProperty("message", "Invalid authentication token");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(SignatureException.class)
    public ProblemDetail handleSignatureException(SignatureException exception) {
        logger.error("[SignatureException] Authentication failed : {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed");
        errorDetail.setProperty("message", "Invalid authentication token");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(EmailConfirmationException.class)
    public ProblemDetail handleEmailConfirmationException(EmailConfirmationException exception) {
        logger.error("[EmailConfirmationException] Authentication failed : {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed");
        errorDetail.setProperty("message", exception.getMessage());
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(AccountAlreadyActivatedException.class)
    public String handleAccountAlreadyActivatedException(AccountAlreadyActivatedException exception) {
        logger.error("[AccountAlreadyActivatedException] : {}", exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        logger.error("[MethodArgumentNotValidException] Constraint violated {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request parameter");
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            logger.error("[MethodArgumentNotValidException] Incorrect {} name : {}", fieldName, errorMessage);
            errorDetail.setProperty(fieldName, errorMessage);
        });
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(InvalidValueException.class)
    public ProblemDetail handleInvalidValueException(InvalidValueException exception) {
        logger.error("Invalid value exception {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        errorDetail.setProperty("message", exception.getMessage());
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(EntityAlreadyPresentException.class)
    public ProblemDetail handleEntityAlreadyPresentException(EntityAlreadyPresentException exception) {
        logger.error(exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        errorDetail.setProperty("message", exception.getMessage());
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException exception) {
        logger.error(exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        errorDetail.setProperty("message", exception.getMessage());
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        logger.error("Got a data integrity violation exception: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        errorDetail.setProperty("message", "An unexpected error occurred");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(MailException.class)
    public ProblemDetail handleMailException(MailException exception) {
        logger.error("[{}] Exception with the mail server: {}", exception.getClass().getSimpleName(), exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        errorDetail.setProperty("message", "An unexpected error occurred with mail server");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception exception) {
        logger.error("[{}] Unhandled exception: {}", exception.getClass().getSimpleName(), exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        errorDetail.setProperty("message", "An unexpected error occurred");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }
}
