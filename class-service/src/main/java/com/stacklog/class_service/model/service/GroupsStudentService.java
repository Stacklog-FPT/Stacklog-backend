package com.stacklog.class_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.stacklog.class_service.model.entities.GroupStudent;
import com.stacklog.class_service.model.repo.GroupsStudentRepo;
import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;

import jakarta.transaction.Transactional;

@Service
public class GroupsStudentService implements IService<GroupStudent> {

    private static final String NAME_SERVICE = "class-service";

    private static final String KAFKA_TOPIC_UPDATE = "class-service.groupstudent.updated";
    private static final String KAFKA_TOPIC_CREATE = "class-service.groupstudent.created";

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    @Autowired GroupsStudentRepo groupsStudentRepo;

    @Autowired KafkaProducer<GroupStudent> kafkaGroupStudentProducer;

    // @Autowired SimpMessagingTemplate messagingTemplate;

    RedisService<GroupStudent> redisGroupStudentService;

    public GroupsStudentService(RedisService<GroupStudent> redisGroupStudentService) {
        this.redisGroupStudentService = redisGroupStudentService;
    }

    @Override
    public GroupStudent delete(String id, String token) {
        return null;    
    }

    @Override
    public List<GroupStudent> getAllByUserId(String token) {
        List<GroupStudent> groupStudents = redisGroupStudentService.getAll(token, NAME_SERVICE);
        if (groupStudents.isEmpty()) {
            groupStudents = groupsStudentRepo.findAll();
            redisGroupStudentService.saveListToRedis(groupStudents, token, NAME_SERVICE);
        }

        return groupStudents;
    }

    @Override
    public GroupStudent getById(String id, String token) {
        GroupStudent groupStudent = redisGroupStudentService.getById(id, token, NAME_SERVICE);
        if (groupStudent == null) {
            groupStudent = groupsStudentRepo.findById(id).orElseThrow();
            redisGroupStudentService.saveToRedis(groupStudent, token, NAME_SERVICE);
        }
        return groupStudent;
    }

    @Override
    public GroupStudent save(GroupStudent e, String token) {
        boolean isCreate = (e.getGroupStudentId() == null || !groupsStudentRepo.existsById(e.getGroupStudentId()));
        GroupStudent newGroupStudent = saveToDB(e, token);
        if (newGroupStudent == null) {
            return null;
        }

        if (isCreate) {
            kafkaGroupStudentProducer.sendMessage(newGroupStudent, KAFKA_TOPIC_CREATE);
        } else {
            kafkaGroupStudentProducer.sendMessage(newGroupStudent, KAFKA_TOPIC_UPDATE);
        }

        redisGroupStudentService.saveToRedis(newGroupStudent, token, NAME_SERVICE);

        // messagingTemplate.convertAndSend("topic/class-service");

        return newGroupStudent;
    }

    @Transactional
    private GroupStudent saveToDB(GroupStudent e, String token) {
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisGroupStudentService.getCurrentUserId(token));
        if (e.getGroupStudentId() == null) {
            e.setCreatedAt(CURRENT_TIME);
            e.setCreatedBy(redisGroupStudentService.getCurrentUserId(token));
            e.setGroupStudentId(UUID.randomUUID().toString());
        }
        return groupsStudentRepo.save(e);
    }

    @Override
    public List<GroupStudent> searchByFields(Predicate<GroupStudent> p, String token) {
        return null;
    }

}
