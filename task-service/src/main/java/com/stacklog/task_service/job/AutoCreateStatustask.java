package com.stacklog.task_service.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.stacklog.task_service.model.entities.StatusTask;
import com.stacklog.task_service.model.service.StatusTaskService;

public class AutoCreateStatustask {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoCreateStatustask.class);

    @Autowired
    StatusTaskService statusTaskService;

    @KafkaListener(topicPattern = "class-service.groupsses.created", groupId = "class-service")
    public void consumerTask(String message) {
        // try {
        //     StatusTask statusTask = new StatusTask();

        //     statusTaskService.save();
        //     LOGGER.info("Message received -> {}", message);
        //     messagingTemplate.convertAndSend("/topic/notification", message);
        // } catch (Exception e) {
        //     LOGGER.error("❌ Error processing Kafka message: {}", message, e);
        // }
    }

}
