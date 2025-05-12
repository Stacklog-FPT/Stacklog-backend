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
import com.stacklog.task_service.model.entities.CheckList;
import com.stacklog.task_service.model.repo.CheckListRepo;

@Service
public class CheckListService implements IService<CheckList> {

    private static final String NAME_SERVICE = "task-service";

    private static final String KAFKA_TOPIC_UPDATE = "task-service.checklist.updated";
    private static final String KAFKA_TOPIC_CREATE = "task-service.checklist.created";

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    @Autowired CheckListRepo checkListRepo;

    @Autowired
    KafkaProducer<CheckList> checkListProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    RedisService<CheckList> redisCheckListService;

    public CheckListService(RedisService<CheckList> redisCheckListService) {
        this.redisCheckListService = redisCheckListService;
    }

    @Override
    public CheckList delete(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<CheckList> getAllByUserId(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllByUserId'");
    }

    @Override
    public CheckList getById(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public CheckList save(CheckList e, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public List<CheckList> searchByFields(Predicate<CheckList> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }
    
}
