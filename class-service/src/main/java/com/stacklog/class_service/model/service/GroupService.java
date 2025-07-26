package com.stacklog.class_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.stacklog.class_service.model.entities.Groupss;
import com.stacklog.class_service.model.repo.GroupsRepo;
import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;

import jakarta.transaction.Transactional;

@Service
public class GroupService implements IService<Groupss> {

    private static final String NAME_SERVICE = "class-service";

    private static final String KAFKA_TOPIC_UPDATE = "class-service.groupsses.updated";
    private static final String KAFKA_TOPIC_CREATE = "class-service.groupsses.created";

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    @Autowired
    GroupsRepo groupsRepo;

    @Autowired
    private KafkaProducer<Groupss> kafkaGroupsProducer;

    // @Autowired private SimpMessagingTemplate messagingTemplate;

    RedisService<Groupss> redisGroupsService;

    public GroupService(RedisService<Groupss> redisGroupsService) {
        this.redisGroupsService = redisGroupsService;
    }

    @Override
    public Groupss delete(String id, String token) {
        return null;
    }

    @Override
    public List<Groupss> getAllByUserId(String token) {
        List<Groupss> groupsses = redisGroupsService.getAll(token, NAME_SERVICE);
        if (groupsses.isEmpty()) {
            groupsses = groupsRepo.findByUserId(redisGroupsService.getCurrentUserId(token));
            redisGroupsService.saveListToRedis(groupsses, token, NAME_SERVICE);
        } 
        return groupsses;
    }

    @Override
    public Groupss getById(String id, String token) {
        Groupss groupss = redisGroupsService.getById(id, token, NAME_SERVICE);
        if (groupss == null) {
            groupss = groupsRepo.findById(id).orElseThrow();
            redisGroupsService.saveToRedis(groupss, token, NAME_SERVICE);
        }
        return groupss;
    }

    @Override
    public Groupss save(Groupss e, String token) {
        boolean isCreate = (e.getGroupsId() == null || !groupsRepo.existsById(e.getGroupsId()));
        Groupss newGroupss = saveToDB(e, token);
        if (newGroupss == null) {
            return null;
        }

        if (isCreate) {
            kafkaGroupsProducer.sendMessage(newGroupss, KAFKA_TOPIC_CREATE);
        } else {
            kafkaGroupsProducer.sendMessage(newGroupss, KAFKA_TOPIC_UPDATE);
        }

        redisGroupsService.saveToRedis(newGroupss, token, NAME_SERVICE);

        // messagingTemplate.convertAndSend("topic/class-service");

        return newGroupss;

    }

    @Transactional
    private Groupss saveToDB(Groupss e, String token) {
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisGroupsService.getCurrentUserId(token));
        if (e.getGroupsId() == null) {
            e.setCreatedAt(CURRENT_TIME);
            e.setCreatedBy(redisGroupsService.getCurrentUserId(token));
            e.setGroupsId(UUID.randomUUID().toString());
        }
        return groupsRepo.save(e);
    }

    @Override
    public List<Groupss> searchByFields(Predicate<Groupss> p, String token) {
        return null;
    }

    public List<Groupss> getAllByClassId(String token, String classesId) {
        List<Groupss> groupsses = redisGroupsService.getAll(token, NAME_SERVICE);
        if (groupsses.isEmpty()) {
            groupsses = groupsRepo.findByClassesClassesId(classesId);
            redisGroupsService.saveListToRedis(groupsses, token, NAME_SERVICE);
        } 
        return groupsses;
    }

}
