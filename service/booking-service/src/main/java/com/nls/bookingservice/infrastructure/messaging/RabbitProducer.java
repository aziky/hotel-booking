package com.nls.bookingservice.infrastructure.messaging;


import com.nls.common.dto.request.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routingKey, NotificationMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }



}
