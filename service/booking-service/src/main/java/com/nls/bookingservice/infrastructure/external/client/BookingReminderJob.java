package com.nls.bookingservice.infrastructure.external.client;

import com.nls.bookingservice.infrastructure.messaging.RabbitProducer;
import com.nls.common.dto.request.NotificationMessage;
import com.nls.common.enumration.QueueName;
import com.nls.common.enumration.TypeEmail;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingReminderJob implements Job {

    RabbitProducer rabbitProducer;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Start executing job");
        String bookingId = context.getMergedJobDataMap().getString("bookingId");
        String userId = context.getMergedJobDataMap().getString("userId");
        String email = context.getMergedJobDataMap().getString("email");

        Map<String, String> payload = new HashMap<>();
        payload.put("bookingId", bookingId);
        payload.put("userId", userId);
        payload.put("email", email);
        payload.put("paymentMethod", "testing-scheduler");

        NotificationMessage notificationMessage = NotificationMessage.builder()
                .to("pcm230304@gmail.com")
                .type(TypeEmail.EMAIL_PAYMENT_SUCCESS.name())
                .payload(payload)
                .build();

        rabbitProducer.sendMessage(
                QueueName.EMAIL_PAYMENT_SUCCESS.getExchangeName(),
                QueueName.EMAIL_PAYMENT_SUCCESS.getRoutingKey(),
                notificationMessage
        );
        log.info("Send to rabbit successfully from the booking service");

    }
}
