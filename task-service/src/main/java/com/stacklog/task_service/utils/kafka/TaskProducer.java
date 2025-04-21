package com.stacklog.task_service.utils.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class TaskProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskProducer.class);

    private final String KAFKA_CREATED_TASK = "task.created";

    private KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public TaskProducer(KafkaTemplate<String, TaskEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(TaskEvent taskEvent) {
        LOGGER.info(String.format("Task event => %s", taskEvent.toString()));

        // create message
        Message<TaskEvent> message = MessageBuilder
                .withPayload(taskEvent)
                .setHeader(KafkaHeaders.TOPIC, KAFKA_CREATED_TASK)
                .build();
        kafkaTemplate.send(message);
        System.out.println("Task event => " + taskEvent.toString());
    }
}
