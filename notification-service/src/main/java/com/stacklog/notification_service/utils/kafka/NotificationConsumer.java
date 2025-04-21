package com.stacklog.notification_service.utils.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.stacklog.notification_service.model.service.NotificationService;
import com.stacklog.notification_service.model.service.UserNoticeService;

@Service
public class NotificationConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserNoticeService userNoticesService;

    // private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @KafkaListener(topics = "task.created", groupId = "notification-service")
    public void consumerTask(String message) {
        LOGGER.info(String.format("Message received -> %s", message));

    }
    // Message received -> {"message":"order is in pending
    // state","status":"PENDING","task":"Task(taskId=6601174048785449949,
    // taskTitle=null, taskDescription=null, groupId=GRP01, documentId=,
    // taskPoint=5, taskDueDate=null, priority=HIGH, statusTask=null,
    // parentTask=null, subtasks=null, assigns=null)"}

}
