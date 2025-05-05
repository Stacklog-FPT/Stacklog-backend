package com.stacklog.class_service.utils.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.stacklog.class_service.model.entities.Groupss;

@Service
public class GroupsKafkaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupsKafkaService.class);

    private KafkaTemplate<String, Groupss> kafkaTemplate;

    public void sendMessage(Groupss gs, String topic) {
        LOGGER.info(String.format("GroupStudent => %s", gs));
        // create message
        Message<Groupss> message = MessageBuilder
                .withPayload(gs)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
        kafkaTemplate.send(message);
    }
}
