package com.stacklog.class_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.stacklog.class_service.model.entities.Classes;
import com.stacklog.class_service.model.repo.ClassesRepo;
import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;

import jakarta.transaction.Transactional;

@Service
public class ClassService implements IService<Classes> {

    private static final String NAME_SERVICE = "class-service";

    private static final String KAFKA_TOPIC_UPDATE = "class-service.classes.updated";
    private static final String KAFKA_TOPIC_CREATE = "class-service.classes.created";

    @Autowired
    private ClassesRepo classesRepo;

    @Autowired
    private KafkaProducer<Classes> kafkaClassProducer;

    // @Autowired private SimpMessagingTemplate messagingTemplate;

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    RedisService<Classes> redisClassService;

    public ClassService(RedisService<Classes> redisClassService) {
        this.redisClassService = redisClassService;
    }

    @Override
    public Classes delete(String id, String token) {
        return null;
    }

    @Override
    public List<Classes> getAllByUserId(String token) {
        List<Classes> classes = redisClassService.getAll(token, NAME_SERVICE);
        if (classes.isEmpty()) {
            classes = classesRepo.findByUserId(redisClassService.getCurrentUserId(token));
            redisClassService.saveListToRedis(classes, token, NAME_SERVICE);
        }
        return classes;
    }

    @Override
    public Classes getById(String id, String token) {
        Classes classes = redisClassService.getById(id, token, NAME_SERVICE);
        if (classes == null) {
            classes = classesRepo.findById(id).orElseThrow();
            redisClassService.saveToRedis(classes, token, NAME_SERVICE);
        }
        return classes;
    }

    @Override
    public Classes save(Classes e, String token) {
        boolean isCreate = (e.getClassesId() == null || !classesRepo.existsById(e.getClassesId()));
        Classes newClasses = saveToDB(e, token);
        if (newClasses == null) {
            return null;
        }

        if (isCreate) {
            // kafka producer
            kafkaClassProducer.sendMessage(e, KAFKA_TOPIC_CREATE);
        } else {
            // kafa producer
            kafkaClassProducer.sendMessage(e, KAFKA_TOPIC_UPDATE);
        }

        redisClassService.saveToRedis(newClasses, token, NAME_SERVICE);

        // messagingTemplate.convertAndSend("/topic/class-service", e);

        return newClasses;
    }

    @Transactional
    private Classes saveToDB(Classes e, String token) {
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisClassService.getCurrentUserId(token));
        if (e.getClassesId() == null) {
            e.setCreatedAt(CURRENT_TIME);
            e.setCreatedBy(redisClassService.getCurrentUserId(token));
            e.setClassesId(UUID.randomUUID().toString());
        }
        return classesRepo.save(e);
    }

    @Override
    public List<Classes> searchByFields(Predicate<Classes> p, String token) {
        List<Classes> classes = redisClassService.getAll(token, NAME_SERVICE);
        if (classes.isEmpty()) {
            classes = classesRepo.findAll();
            redisClassService.saveListToRedis(classes, token, NAME_SERVICE);
        }
        return classes.stream().filter(p).toList();
    }

}
