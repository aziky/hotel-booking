package com.nls.notificationservice.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nls.common.enumration.QueueName;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue emailConfirmOtpQueue() {
        return new Queue(QueueName.EMAIL_CONFIRM_OTP.getQueueName(), true);
    }

    @Bean
    public Queue emailConfirmPaymentQueue() {
        return new Queue(QueueName.EMAIL_CONFIRM_PAYMENT.getQueueName(), true);
    }

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange("email.exchange");
    }

    @Bean
    public Binding emailConfirmOtpBinding() {
        return BindingBuilder
                .bind(emailConfirmOtpQueue())
                .to(emailExchange())
                .with(QueueName.EMAIL_CONFIRM_OTP.getRoutingKey());
    }

    @Bean
    public Binding emailConfirmPaymentBinding() {
        return BindingBuilder
                .bind(emailConfirmPaymentQueue())
                .to(emailExchange())
                .with(QueueName.EMAIL_CONFIRM_PAYMENT.getRoutingKey());
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}