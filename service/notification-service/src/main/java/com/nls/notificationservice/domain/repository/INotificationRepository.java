package com.nls.notificationservice.domain.repository;

import com.nls.notificationservice.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface INotificationRepository extends JpaRepository<Notification, UUID> {
}
