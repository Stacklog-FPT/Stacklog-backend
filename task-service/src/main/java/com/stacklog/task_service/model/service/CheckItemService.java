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
import com.stacklog.task_service.model.entities.CheckItem;
import com.stacklog.task_service.model.repo.CheckItemRepo;

@Service
public class CheckItemService implements IService<CheckItem> {

    private static final String NAME_SERVICE = "task-service";

    private static final String KAFKA_TOPIC_UPDATE = "task-service.checkitem.updated";
    private static final String KAFKA_TOPIC_CREATE = "task-service.checkitem.created";

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    @Autowired CheckItemRepo checkItemRepo;

    @Autowired
    KafkaProducer<CheckItem> checkItemProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    RedisService<CheckItem> redisCheckItemService;

    public CheckItemService(RedisService<CheckItem> redisCheckItemService) {
        this.redisCheckItemService = redisCheckItemService;
    }

    @Override
    public CheckItem delete(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<CheckItem> getAllByUserId(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllByUserId'");
    }

    @Override
    public CheckItem getById(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public CheckItem save(CheckItem e, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public List<CheckItem> searchByFields(Predicate<CheckItem> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }
}
