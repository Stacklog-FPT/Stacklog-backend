package com.stacklog.task_service.utils.kafka;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.Task;

@Service
public class KafkaService {

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;


    public void sendNotification(String statisticMessage) {
        Statastic stat = new Statastic(statisticMessage, LocalDateTime.now());
        kafkaTemplate.send("notification", new Task());
        kafkaTemplate.send("statistic", stat);
    }



}
