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
import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.repo.TaskRepo;

@Service
public class TaskService implements IService<Task> {

    private static final String NAME_SERVICE = "task-service";

    private static final String KAFKA_TOPIC_UPDATE = "task-service.task.updated";
    private static final String KAFKA_TOPIC_CREATE = "task-service.task.created";

    @Autowired
    TaskRepo taskRepo;

    @Autowired
    KafkaProducer<Task> kafkaTaskProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    RedisService<Task> redisTaskService;

    public TaskService(RedisService<Task> redisTaskService) {
        this.redisTaskService = redisTaskService;
    }

    @Override
    public Task delete(String id, String token) {
        return null;
    }

    @Override
    public List<Task> getAllByUserId(String token) {
        List<Task> tasks = redisTaskService.getAll(token, NAME_SERVICE);
        if (tasks.isEmpty()) {
            tasks = taskRepo.findByUserId(redisTaskService.getCurrentUserId(token));
            redisTaskService.saveListToRedis(tasks, token, NAME_SERVICE);
        }
        return tasks;
    }

    public List<Task> getAllByGroupId(String token, String groupId) {
        String currentUserId = redisTaskService.getCurrentUserId(token);
        String groupIndexKey = redisTaskService.getCustomIndexKey(currentUserId, NAME_SERVICE, "group:" + groupId);

        List<Task> tasks = redisTaskService.getAll(token, groupIndexKey);
        if (tasks.isEmpty()) {
            tasks = taskRepo.findByGroupId(groupId);
            redisTaskService.saveListToRedis(tasks, token, groupIndexKey);
        }
        return tasks;
    }

    @Override
    public Task getById(String id, String token) {
        Task task = redisTaskService.getById(id, token, NAME_SERVICE);
        if (task == null) {
            task = taskRepo.findById(id).orElse(null);
            if (task != null) {
                redisTaskService.saveToRedis(task, token, NAME_SERVICE);
            }
        }
        return task;
    }

    @Override
    @Transactional
    public Task save(Task e, String token) {
        LocalDateTime now = CommonFunction.getCurrentTime();
        boolean isCreate = (e.getTaskId() == null || !taskRepo.existsById(e.getTaskId()));
        String currentUserId = redisTaskService.getCurrentUserId(token);

        e.setUpdateAt(now);
        e.setUpdateBy(currentUserId);
        if (isCreate) {
            e.setCreatedAt(now);
            e.setCreatedBy(currentUserId);
            e.setTaskId(UUID.randomUUID().toString());
        }

        e = taskRepo.save(e);
        if (e.getGroupId() != null) {
            String groupKey = redisTaskService.getCustomIndexKey(currentUserId, NAME_SERVICE, "group:" + e.getGroupId());
            redisTaskService.saveToRedis(e, token, groupKey);
        }
        kafkaTaskProducer.sendMessage(e, isCreate ? KAFKA_TOPIC_CREATE : KAFKA_TOPIC_UPDATE);
        messagingTemplate.convertAndSend("/topic/task-service", e);

        return e;

    }

    @Override
    public List<Task> searchByFields(Predicate<Task> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }

}
