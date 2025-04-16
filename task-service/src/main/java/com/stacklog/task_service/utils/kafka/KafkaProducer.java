package com.stacklog.task_service.utils.kafka;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, TaskEvent> kafkaTemplate;

    private static final String TOPIC = "task-service";

    public void sendMessage(TaskEvent taskEvent) {
        String key = "task-" + taskEvent.getTaskId();
        taskEvent.setCreatedAt(LocalDateTime.now());
        kafkaTemplate.send(TOPIC, key, taskEvent);
        System.out.println("✅ Sent Kafka message: key=" + key + ", event=" + taskEvent);
    }
} 