package com.stacklog.task_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.task_service.model.entities.TaskAssign;
import com.stacklog.task_service.model.repo.TaskAssignRepo;

@Service
public class TaskAssignService implements IService<TaskAssign> {

    private static final String NAME_SERVICE = "task-service";

    private static final String KAFKA_TOPIC_UPDATE = "task-service.taskassign.updated";
    private static final String KAFKA_TOPIC_CREATE = "task-service.taskassign.created";

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    @Autowired
    TaskAssignRepo taskAssignRepo;

    @Autowired
    KafkaProducer<TaskAssign> taskAssignProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    RedisService<TaskAssign> redisTaskAssignService;

    public TaskAssignService(RedisService<TaskAssign> redisTaskAssignService) {
        this.redisTaskAssignService = redisTaskAssignService;
    }

    @Override
    public TaskAssign delete(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<TaskAssign> getAllByUserId(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllByUserId'");
    }

    @Override
    public TaskAssign getById(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public TaskAssign save(TaskAssign e, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public List<TaskAssign> searchByFields(Predicate<TaskAssign> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }
    
}
