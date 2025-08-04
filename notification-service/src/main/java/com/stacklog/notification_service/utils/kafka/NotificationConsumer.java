package com.stacklog.notification_service.utils.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.stacklog.notification_service.model.repo.NotificationRepo;
import com.stacklog.notification_service.model.service.NotificationService;
import com.stacklog.notification_service.model.service.UserNoticeService;

@Service
public class NotificationConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserNoticeService userNoticesService;

    @Autowired
    NotificationRepo notificationRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @KafkaListener(topicPattern = "task-service\\..*\\..*", groupId = "notification-service")
    public void consumerTask(String message) {
        try {
            notificationService.save(message);
            LOGGER.info("Message received -> {}", message);
            messagingTemplate.convertAndSend("/topic/notification", message);
        } catch (Exception e) {
            LOGGER.error("❌ Error processing Kafka message: {}", message, e);
        }
    }

}
