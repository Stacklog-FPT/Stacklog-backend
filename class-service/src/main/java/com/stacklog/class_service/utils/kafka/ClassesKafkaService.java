package com.stacklog.class_service.utils.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.stacklog.class_service.model.entities.Classes;


@Service
public class ClassesKafkaService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupsKafkaService.class);

    private KafkaTemplate<String, Classes> kafkaTemplate;

    public void sendMessage(Classes gs, String topic) {
        LOGGER.info(String.format("GroupStudent => %s", gs));
        // create message
        Message<Classes> message = MessageBuilder
                .withPayload(gs)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
        kafkaTemplate.send(message);
    }

}
