package com.kuro.expensetracker.services.user;

import com.kuro.expensetracker.models.EmailConfirmationToken;
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

    public void sendConfirmationEmail(EmailConfirmationToken emailConfirmationToken) throws MessagingException, MailAuthenticationException {
        //MIME - HTML message
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(emailConfirmationToken.getUser().getEmail());
        helper.setSubject("Confirm you E-Mail - " + appName);
        helper.setText("<html>" +
                        "<body>" +
                        "<h2>Dear " + emailConfirmationToken.getUser().getName() + ",</h2>"
                        + "<br/> We're excited to have you get started. " +
                        "Please click on below link to confirm your account."
                        + "<br/> " + generateConfirmationLink(emailConfirmationToken.getToken()) +
                        "<br/> Or copy and paste this url : " + generateConfirmationRawLink(emailConfirmationToken.getToken()) +
                        "<br/> Regards,<br/>" +
                        appName + " team" +
                        "</body>" +
                        "</html>"
                , true);
        sender.send(message);
    }

    private String generateConfirmationLink(String token) {
        return String.format("<a href=http://%s:%s/%s/users/confirm-email?token=%s>Confirm Email</a>", appHost, appPort, apiPrefix, token);
    }

    private String generateConfirmationRawLink(String token) {
        return String.format("%s:%s/%s/users/confirm-email?token=%s", appHost, appPort, apiPrefix, token);
    }
}
