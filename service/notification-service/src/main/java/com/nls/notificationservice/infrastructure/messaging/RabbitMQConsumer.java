package com.nls.notificationservice.infrastructure.messaging;

import com.nls.common.dto.request.NotificationMessage;
import com.nls.notificationservice.application.INotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMQConsumer {

    INotificationService notificationService;

    @RabbitListener(queues = "#{emailConfirmOtpQueue.name}")
    public void consumeEmailConfirmOtp(NotificationMessage message) {
        log.info("Received email confirmation OTP message: {}", message);
        notificationService.sendEmail(message);
    }

    @RabbitListener(queues = "#{emailPaymentSuccessQueue.name}")
    public void consumeEmailPaymentSuccess(NotificationMessage message) {
        log.info("Received email payment success message: {}", message);
        notificationService.sendEmail(message);
    }

    @RabbitListener(queues = "#{emailForgetPasswordQueue.name}")
    public void consumeEmailResetPassword(NotificationMessage message) {
        log.info("Received email reset password message: {}", message);
        notificationService.sendEmail(message);
    }

}