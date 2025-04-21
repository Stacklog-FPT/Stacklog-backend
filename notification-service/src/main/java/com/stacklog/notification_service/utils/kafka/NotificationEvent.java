package com.stacklog.notification_service.utils.kafka;

import com.stacklog.notification_service.model.entities.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    private String message;
    private String status;
    private Notification notification;
}
