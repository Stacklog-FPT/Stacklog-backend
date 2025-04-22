package com.stacklog.notification_service.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.stacklog.notification_service.model.entities.Notification;
import com.stacklog.notification_service.model.repo.NotificationRepo;

@Service
public class NotificationService {
    
    @Autowired
    NotificationRepo notificationRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public List<Notification> getAll() {
        return notificationRepo.findAll();
    }

    public Notification getById(Long id){
        return notificationRepo.findById(id).get();
    }

    public List<Notification> saveAll(List<Notification> list) {
        
        return notificationRepo.saveAll(list);
    }

    public void save(String message) {
        messagingTemplate.convertAndSend("/topic/notification", Map.of("message", message));
    }

}
