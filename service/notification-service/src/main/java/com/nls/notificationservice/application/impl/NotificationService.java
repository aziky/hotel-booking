package com.nls.notificationservice.application.impl;

import com.nls.common.dto.request.NotificationMessage;
import com.nls.notificationservice.application.INotificationService;
import com.nls.notificationservice.domain.entity.Notification;
import com.nls.notificationservice.domain.entity.Template;
import com.nls.notificationservice.domain.repository.INotificationRepository;
import com.nls.notificationservice.domain.repository.ITemplateRepository;
import com.nls.notificationservice.infrastructure.external.client.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService implements INotificationService {

    EmailService emailService;
    ITemplateRepository templateRepository;
    INotificationRepository notificationRepository;

    @Override
    public void sendEmail(NotificationMessage notificationMessage) {
        try {
            log.info("Sending email notification to: {}", notificationMessage.to());
            Template template = templateRepository.findByTypeAndStatusIsTrue(notificationMessage.type());

            if (template == null) { throw new EntityNotFoundException("Template not found");}

            String subject = templateMapping(template.getSubject(), notificationMessage.payload());
            String body = templateMapping(template.getContent(), notificationMessage.payload());

            Map<String, String> payload = new HashMap<>();
            payload.put("subject", subject);
            payload.put("body", body);

            notificationMessage = notificationMessage.withPayload(payload);

            emailService.sendEmail(notificationMessage);
            Notification notification = Notification.builder()
                    .emailReceiver(notificationMessage.to())
                    .templateId(template.getId())
                    .build();

            notificationRepository.save(notification);
            log.info("Save notification successfully");
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
        }
    }

    private String templateMapping(String source, Map<String, String> payload) {
        StringSubstitutor sbs = new StringSubstitutor(payload);
        return sbs.replace(source);
    }
}
