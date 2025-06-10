package com.nls.userservice.infrastructure.messaging;

import com.nls.common.dto.request.NotificationMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitProducer {

    RabbitTemplate rabbitTemplate;

    public void sendEmail(String exchangeName, String routingKey, NotificationMessage message ) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }

}
