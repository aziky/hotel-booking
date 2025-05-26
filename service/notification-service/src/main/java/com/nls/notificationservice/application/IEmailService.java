package com.nls.notificationservice.application;

import com.nls.common.dto.request.NotificationMessage;

public interface IEmailService {

    void sendEmail(NotificationMessage notificationMessage);

}
