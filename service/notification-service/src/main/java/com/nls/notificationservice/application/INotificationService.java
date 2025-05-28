package com.nls.notificationservice.application;

import com.nls.common.dto.request.NotificationMessage;

public interface INotificationService {

    void sendEmail(NotificationMessage notificationMessage);


}
