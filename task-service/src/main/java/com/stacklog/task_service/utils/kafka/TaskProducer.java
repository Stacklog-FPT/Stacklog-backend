package com.stacklog.task_service.utils.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.StatusTask;
import com.stacklog.task_service.model.entities.Task;

@Service
public class TaskProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskProducer.class);

    private KafkaTemplate<String, Task> kafkaTemplate;

    public TaskProducer(KafkaTemplate<String, Task> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Task task, String topic) {
        LOGGER.info(String.format("Task => %s", task));

        // create message
        Message<Task> message = MessageBuilder
                .withPayload(task)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
        kafkaTemplate.send(message);
    }

    public void sendMessage(StatusTask statusTask, String topic) {
        // create message
        Message<StatusTask> message = MessageBuilder
                .withPayload(statusTask)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
        kafkaTemplate.send(message);
    }
}
