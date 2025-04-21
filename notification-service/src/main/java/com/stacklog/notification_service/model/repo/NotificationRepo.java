package com.stacklog.notification_service.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stacklog.notification_service.model.entities.Notification;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    
}
