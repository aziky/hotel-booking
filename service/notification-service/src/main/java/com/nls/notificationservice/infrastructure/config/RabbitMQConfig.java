package com.nls.notificationservice.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nls.common.enumration.QueueName;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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
    public Queue emailPaymentSuccessQueue() {
        return new Queue(QueueName.EMAIL_PAYMENT_SUCCESS.getQueueName(), true);
    }

    @Bean
    public Queue emailForgetPasswordQueue() { return new Queue(QueueName.EMAIL_FORGET_PASSWORD.getQueueName(), true);}

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
                .bind(emailPaymentSuccessQueue())
                .to(emailExchange())
                .with(QueueName.EMAIL_PAYMENT_SUCCESS.getRoutingKey());
    }

    @Bean
    public Binding emailResetPasswordBinding() {
        return BindingBuilder
                .bind(emailForgetPasswordQueue())
                .to(emailExchange())
                .with(QueueName.EMAIL_FORGET_PASSWORD.getRoutingKey());
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}