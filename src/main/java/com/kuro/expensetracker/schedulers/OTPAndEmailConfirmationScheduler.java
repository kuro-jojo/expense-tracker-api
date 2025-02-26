package com.kuro.expensetracker.schedulers;

import com.kuro.expensetracker.services.user.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OTPAndEmailConfirmationScheduler {
    private final AuthenticationService authenticationService;

    private final Logger logger = LoggerFactory.getLogger(OTPAndEmailConfirmationScheduler.class);

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    public void removeExpiredConfirmationEmailTokens() {
        logger.info("Removing expired confirmation email tokens.");
        var ids = authenticationService.removeExpiredConfirmationTokens();
        if (ids.isEmpty()) {
            logger.info("No expired confirmation email tokens removed.");
        } else {
            ids.forEach(id -> logger.info("Removed expired confirmation email token with id #{}.", id));
        }
    }

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    public void removeExpiredOTPs() {
        logger.info("Removing expired OTPs.");
        var ids = authenticationService.removeExpiredOTPs();
        if (ids.isEmpty()) {
            logger.info("No expired OTPs removed.");
        } else {
            ids.forEach(id -> logger.info("Removed expired OTP with id #{}.", id));
        }
    }
}