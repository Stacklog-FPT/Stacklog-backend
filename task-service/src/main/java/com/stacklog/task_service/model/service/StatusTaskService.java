package com.stacklog.task_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return null;
    }

    public List<StatusTask> getAllByGroupId(String token, String groupId) {
        List<StatusTask> statusTasks = redisStatusTaskService.getAll(token, NAME_SERVICE);
        if (statusTasks.isEmpty() || statusTasks == null) {
            statusTasks = statusTaskRepo.findAllByGroupId(groupId);
            redisStatusTaskService.saveListToRedis(statusTasks, token, NAME_SERVICE);
        }
        return statusTasks;
    }

    @Override
    public StatusTask getById(String id, String token) {
        StatusTask statusTask = redisStatusTaskService.getById(id, token, NAME_SERVICE);
        if (statusTask == null) {
            statusTask = statusTaskRepo.findById(id).orElseThrow();
            redisStatusTaskService.saveToRedis(statusTask, token, NAME_SERVICE);
        }
        return statusTask;
    }

    @Override
    @Transactional
    public StatusTask save(StatusTask e, String token) {
        boolean isCreate = (e.getStatusTaskId() == null || !statusTaskRepo.existsById(e.getStatusTaskId()));
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisStatusTaskService.getCurrentUserId(token));
        if (e.getStatusTaskId() == null) {
            e.setCreatedAt(CURRENT_TIME);
            e.setCreatedBy(redisStatusTaskService.getCurrentUserId(token));
            e.setStatusTaskId(UUID.randomUUID().toString());
        }
        if (isCreate) {
            statusTaskProducer.sendMessage(e, KAFKA_TOPIC_CREATE);
        } else {
            statusTaskProducer.sendMessage(e, KAFKA_TOPIC_UPDATE);
        }

        redisStatusTaskService.saveToRedis(e, token, NAME_SERVICE);

        messagingTemplate.convertAndSend("/topic/task-service", e);

        return e;
    }

    @Override
    public List<StatusTask> searchByFields(Predicate<StatusTask> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }
    
}
