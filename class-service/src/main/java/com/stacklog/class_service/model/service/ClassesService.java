package com.stacklog.class_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.stacklog.class_service.model.entities.Classes;
import com.stacklog.class_service.model.repo.ClassesRepo;
import com.stacklog.class_service.utils.kafka.ClassesKafkaService;
import com.stacklog.class_service.utils.redis.RedisService;

@Service
public class ClassesService {

    private final String KAFKA_TOPIC_CREATE_CLASS = "";
    private final String KAFKA_TOPIC_UPDATE_CLASS = "";
    private final String KAFKA_TOPIC_DELETE_CLASS = "";

    @Autowired
    ClassesRepo classesRepo;

    RedisService<Classes> redisService;

    ClassesKafkaService classesKafkaService;

    @Autowired private SimpMessagingTemplate messagingTemplate;

    public ClassesService(RedisService<Classes> redisService, ClassesKafkaService classesKafkaService) {
        this.redisService = redisService;
        this.classesKafkaService = classesKafkaService;
    }

    public List<Classes> getAll() {
        // List<Classes> classes = redisService.getDatas();
        return classesRepo.findAll();
    }

    public List<Classes> getAllByLectureId(String lectureId) {
        List<Classes> classes = redisService.getDatas();
        if (classes.isEmpty()) {
            Classes classesEntity = new Classes();
            classesEntity.setLectureId(lectureId);
            Example<Classes> example = Example.of(classesEntity);
            classes = classesRepo.findAll(example);
        }

        redisService.saveListToRedis(classes);

        return classes;
    }

    public List<Classes> getAllByUserId(String userId) {
        
    }

    public Classes save(Classes classes, String typeAction) {
        String topic = KAFKA_TOPIC_UPDATE_CLASS;
        switch (typeAction) {
            case "create":
                classes.setClassesId(UUID.randomUUID().toString());
                classes.setCreatedAt(LocalDateTime.now());
                classes.setCreatedBy(redisService.getCurrentUserId());
                topic = KAFKA_TOPIC_CREATE_CLASS;
            case "update":
                classes.setUpdateAt(LocalDateTime.now());
                classes.setUpdateBy(redisService.getCurrentUserId());
                break;
            default:
                break;
        }

        Classes newClasses = classesRepo.save(classes);
        classesKafkaService.sendMessage(newClasses, topic);

        redisService.saveToRedis(newClasses, newClasses.getClassesId(), "PENDING_WRITE");

        messagingTemplate.convertAndSend("/topic/classes", newClasses);

        return newClasses;

    }

    public Classes delete(String classId) {
        return null;
    }


}
