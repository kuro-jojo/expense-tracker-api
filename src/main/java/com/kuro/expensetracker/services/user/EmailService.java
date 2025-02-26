package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.models.ConfirmationEmailToken;
import com.kuro.expensetracker.models.OTP;
import com.kuro.expensetracker.models.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender sender;
    @Value("${config.app-name}")
    private String appName;
    @Value("${server.port}")
    private String appPort;
    @Value("${config.app-host:localhost}")
    private String appHost;
    @Value("${api.prefix}")
    private String apiPrefix;

    public void sendConfirmationEmail(ConfirmationEmailToken confirmationEmailToken) throws MessagingException, MailAuthenticationException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(confirmationEmailToken.getUser().getEmail());
        helper.setSubject("Confirm you E-Mail - " + appName);
        helper.setText("<html>" +
                        "<body>" +
                        "<h2>Dear " + confirmationEmailToken.getUser().getName() + ",</h2>"
                        + "<p> We're excited to have you get started. " +
                        "Please click on below link to confirm your account."
                        + "<br/> " + generateConfirmationLink(confirmationEmailToken.getToken()) +
                        "<br/> Or copy and paste this url : " + generateConfirmationRawLink(confirmationEmailToken.getToken()) +
                        "<br/> Regards,<br/></p>" +
                        appName + " team" +
                        "</body>" +
                        "</html>"
                , true);
        sender.send(message);
    }

    public void sendConfirmationEmail(OTP otp, User user) throws MessagingException, MailAuthenticationException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(otp.getEmail());
        helper.setSubject("Your One-Time Password (OTP) for Secure Access - " + appName);
        helper.setText("<html>" +
                        "<body>" +
                        "<h2>Dear " + user.getName() + ",</h2>"
                        + "<p>We're excited to have you get started. " +
                        "please use the following One-Time Password (OTP) : "
                        + "<br/> <b> OTP : [" + otp.getOtp() + "]</b>" +
                        "<br/> Regards,<br/></p>" +
                        appName + " team" +
                        "</body>" +
                        "</html>"
                , true);
        sender.send(message);
    }

    public void sendAccountDeletedEmail(User user) throws MessagingException, MailAuthenticationException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(user.getEmail());
        helper.setSubject("Your " + appName + " account has been deleted");
        helper.setText("<html>" +
                        "<body>" +
                        "<h2>Dear " + user.getName() + ",</h2>"
                        + "<p>We want to inform you that your account with " + appName + " has been deleted.\n" +
                        "\n <br/>" +
                        "If this was intentional, no further action is needed. " +
                        "However, if you believe this was a mistake or have any questions, " +
                        "please contact our support team at " + appName + "@support.com .\n" +
                        "\n<br/>" +
                        "Thank you for being a part of " + appName + "." +
                        "<br/> Regards,<br/></p>" +
                        appName + " team" +
                        "</body>" +
                        "</html>"
                , true);
        sender.send(message);
    }

    private String generateConfirmationLink(String token) {
        return String.format("<a href=http://%s:%s/%s/auth/confirm-email?token=%s>Confirm Email</a>", appHost, appPort, apiPrefix, token);
    }

    private String generateConfirmationRawLink(String token) {
        return String.format("%s:%s/%s/auth/confirm-email?token=%s", appHost, appPort, apiPrefix, token);
    }

}
