package com.nls.notificationservice.infrastructure.external.client;

import com.nls.common.dto.request.NotificationMessage;
import com.nls.notificationservice.application.IEmailService;
import com.nls.notificationservice.infrastructure.properties.EmailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService implements IEmailService {

    JavaMailSender mailSender;
    EmailProperties emailProperties;

    public void sendEmail(NotificationMessage message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(emailProperties.email(), emailProperties.name());
            helper.setTo(message.to());

            String subject = message.payload().getOrDefault("subject", "Notification from NLS");
            String body = message.payload().getOrDefault("body", "No content provided");

            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(mimeMessage);
            log.info("Email sent successfully to: {}, type: {}", message.to(), message.type());
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", message.to(), e);
        } catch (Exception e) {
            log.error("Unexpected error when sending email to: {}", message.to(), e);
        }
    }
}
