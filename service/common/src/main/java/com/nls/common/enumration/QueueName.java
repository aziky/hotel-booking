package com.nls.common.enumration;

public enum QueueName {

    EMAIL_CONFIRM_OTP(
            "email.confirm.otp.queue",
            "email.exchange",
            "email.confirm.otp"
    ),

    EMAIL_PAYMENT_SUCCESS(
            "email.confirm.payment.queue",
            "email.exchange",
            "email.confirm.payment"
    ),

    EMAIL_FORGET_PASSWORD(
            "email.forget.password.queue",
            "email.exchange",
            "email.forget.password"
    );

    private final String queueName;
    private final String exchangeName;
    private final String routingKey;

    QueueName(String queueName, String exchangeName, String routingKey) {
        this.queueName = queueName;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getRoutingKey() {
        return routingKey;
    }
}
