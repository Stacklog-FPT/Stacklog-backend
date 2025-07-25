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

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

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
        List<Task> tasks = redisTaskService.getAll(token, NAME_SERVICE);
        if (tasks.isEmpty()) {
            tasks = taskRepo.findByGroupId(groupId);
            redisTaskService.saveListToRedis(tasks, token, NAME_SERVICE);
        }
        return tasks.stream().filter(t -> t.getGroupId().equals(groupId)).toList();
    }

    @Override
    public Task getById(String id, String token) {
        Task task = redisTaskService.getById(id, token, NAME_SERVICE);
        if (task == null) {
            task = taskRepo.findById(id).orElseThrow();
            redisTaskService.saveToRedis(task, token, NAME_SERVICE);
        }
        return task;
    }

    @Override
    @Transactional
    public Task save(Task e, String token) {
        boolean isCreate = (e.getTaskId() == null || !taskRepo.existsById(e.getTaskId()));
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisTaskService.getCurrentUserId(token));
        if (e.getTaskId() == null || e.getTaskId().isBlank()) {
            e.setCreatedAt(CURRENT_TIME);
            e.setCreatedBy(redisTaskService.getCurrentUserId(token));
            e.setTaskId(UUID.randomUUID().toString());
        }
        if (isCreate) {
            kafkaTaskProducer.sendMessage(e, KAFKA_TOPIC_CREATE);
        } else {
            kafkaTaskProducer.sendMessage(e, KAFKA_TOPIC_UPDATE);
        }

        redisTaskService.saveToRedis(e, token, NAME_SERVICE);

        messagingTemplate.convertAndSend("/topic/task-service", e);

        taskRepo.save(e);

        return e;

    }


    @Override
    public List<Task> searchByFields(Predicate<Task> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }

}
