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
import com.stacklog.task_service.model.entities.StatusTask;
import com.stacklog.task_service.model.repo.StatusTaskRepo;

@Service
public class StatusTaskService implements IService<StatusTask> {

    private static final String NAME_SERVICE = "task-service";

    private static final String KAFKA_TOPIC_UPDATE = "task-service.statustask.updated";
    private static final String KAFKA_TOPIC_CREATE = "task-service.statustask.created";

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    @Autowired StatusTaskRepo statusTaskRepo;

    @Autowired
    KafkaProducer<StatusTask> statusTaskProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    RedisService<StatusTask> redisStatusTaskService;

    public StatusTaskService(RedisService<StatusTask> redisStatusTaskService) {
        this.redisStatusTaskService = redisStatusTaskService;
    }

    @Override
    public StatusTask delete(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<StatusTask> getAllByUserId(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllByUserId'");
    }

    @Override
    public StatusTask getById(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public StatusTask save(StatusTask e, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public List<StatusTask> searchByFields(Predicate<StatusTask> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }
    
}
