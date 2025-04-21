package com.stacklog.notification_service.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "UserNotice")
public class UserNotice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNoticeId;

    private String sendTo;
    private Boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "notificationId")
    private Notification notification;

}
