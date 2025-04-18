package com.stacklog.task_service.utils.kafka;

import org.apache.kafka.clients.admin.NewTopic;
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

    private NewTopic topic;

    private KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public TaskProducer(NewTopic topic, KafkaTemplate<String, TaskEvent> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(TaskEvent taskEvent) {
        LOGGER.info(String.format("Task event => %s", taskEvent.toString()));

        // create message
        Message<TaskEvent> message = MessageBuilder
                                            .withPayload(taskEvent)
                                            .setHeader(KafkaHeaders.TOPIC, topic.name())
                                            .build();
        kafkaTemplate.send(message);

    }
}
