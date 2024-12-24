package com.kuro.expensetracker.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException exception) {
        logger.error("[BadCredentialsException] Authentication failed: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed");
        errorDetail.setProperty("message", exception.getMessage());
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(UsernameNotFoundException exception) {
        logger.error("[UsernameNotFoundException] Authentication failed: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed");
        errorDetail.setProperty("message", exception.getMessage());
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ProblemDetail handleCredentialsExpiredException(CredentialsExpiredException exception) {
        logger.error("[CredentialsExpiredException] Authentication failed: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed");
        errorDetail.setProperty("message", exception.getMessage());
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ProblemDetail handleJwtExceptions(MalformedJwtException exception) {
        logger.error("[MalformedJwtException] Authentication failed : {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed");
        errorDetail.setProperty("message", String.format("Error while parsing the jwt token: %s", exception.getMessage()));
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwtException(ExpiredJwtException exception) {
        logger.error("[ExpiredJwtException] Authentication failed : {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed");
        errorDetail.setProperty("message", "Jwt Token expired");
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

    @ExceptionHandler(AccountStatusException.class)
    public ProblemDetail handleAccountStatusException(AccountStatusException exception) {
        logger.error("[AccountStatusException] Account status error: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Account locked or disabled");
        errorDetail.setProperty("message", "The account is locked or inactive");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        logger.error("[AccessDeniedException] Access denied: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied");
        errorDetail.setProperty("message", "You do not have permission to access this resource");
        errorDetail.setProperty("timestamp", Instant.now());
        return errorDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        logger.error("[MethodArgumentNotValidException] Constraint violated {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request parameter");
//        errorDetail.setProperty("message", exception.getMessage());
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorDetail.setProperty(fieldName, errorMessage);
        });
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

    @ExceptionHandler(InvalidValueException.class)
    public ProblemDetail handleInvalidValueException(InvalidValueException exception) {
        logger.error("Invalid value exception {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
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
