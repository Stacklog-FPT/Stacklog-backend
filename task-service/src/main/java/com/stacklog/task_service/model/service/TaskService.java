package com.stacklog.task_service.model.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.entities.TaskAssign;
import com.stacklog.task_service.model.repo.TaskRepo;
import com.stacklog.task_service.utils.kafka.TaskProducer;
import com.stacklog.task_service.utils.redis.RedisService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskService implements IService<Task> {

    private final String KAFKA_CREATED_TASK = "task-service.task.created";
    private final String KAFKA_UPDATED_TASK = "task-service.task.updated";
    private final String KAFKA_UPDATED_CHECKLIST = "task-service.checklist.updated";
    private final String KAFKA_UPDATED_CHECKITEM = "task-service.checkitem.updated";
    private final String KAFKA_UPDATED_TASKASSIGN = "task-service.taskassign.updated";
    private final String KAFKA_UPDATED_TASKSTATUSTASK = "task-service.taksstatustask.updated";

    RedisService<Task> redisTaskService;

    TaskProducer taskProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public TaskService(RedisService<Task> redisTaskService, TaskProducer taskProducer) {
        this.redisTaskService = redisTaskService;
        this.taskProducer = taskProducer;
    }

    @Autowired
    TaskRepo taskRepo;

    @Override
    public List<Task> getAll() {
        List<Task> tasks = redisTaskService.getDatas();

        if (!tasks.isEmpty()) {
            log.info("✅ Loaded {} tasks from Redis cache", tasks.size());
        } else {
            tasks = getByUserId();
            redisTaskService.saveListToRedis(tasks);

        }
        return tasks;
    }

    private List<Task> getByUserId() {
        String currentUserId = redisTaskService.getCurrentUserId();
        return taskRepo.findAll().stream().filter((Task task) -> task.getAssigns().stream()
                .allMatch((TaskAssign taskAssign) -> taskAssign.getAssignTo().equals(currentUserId))).toList();
    }

    @Override
    public Task getById(String id) {
        Task task = redisTaskService.getDataById(id);
        if (task != null) {
            log.info("✅ Loaded task from Redis cache", task.toString());
        } else {
            task = taskRepo.findById(id).orElse(null);
            if (task != null) {
                redisTaskService.saveToRedis(task, task.getTaskId().toString(), "PENDING_WRITE");
            }
        }
        return task;
    }

    @Override
    public Task save(Task e) {
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisTaskService.getCurrentUserId());
        String topic = KAFKA_UPDATED_TASK;
        if (e.getTaskId() == null || getById(e.getTaskId()) == null) {
            String eId = UUID.randomUUID().toString();
            e.setTaskId(eId);
            e.setCreatedAt(CURRENT_TIME);
            e.setCreatedBy(redisTaskService.getCurrentUserId());
            topic = KAFKA_CREATED_TASK;
        }

        // websocket
        messagingTemplate.convertAndSend("/topic/task", e);

        // kafka
        taskProducer.sendMessage(e, topic);

        // db
        taskRepo.save(e);

        // redis
        
        return redisTaskService.saveToRedis(e, e.getTaskId().toString(), "PENDING_WRITE");

    }

    @Override
    public Task remove(String id) {
        return null;
    }

    public List<Task> getByGroupId(String groupId) {
        return getAll().stream()
                .filter(task -> task.getGroupId().equals(groupId))
                .toList();
    }

    public Task sendNotification(Task task, String topic) {
        task.setUpdateAt(CURRENT_TIME);
        task.setUpdateBy(redisTaskService.getCurrentUserId());
        switch (topic) {
            case "task":
                taskProducer.sendMessage(task, KAFKA_UPDATED_TASK);
                break;
            case "checklist":
                taskProducer.sendMessage(task, KAFKA_UPDATED_CHECKLIST);
                break;
            case "checkitem":
                taskProducer.sendMessage(task, KAFKA_UPDATED_CHECKITEM);
                break;
            case "taskassign":
                taskProducer.sendMessage(task, KAFKA_UPDATED_TASKASSIGN);
                break;
            case "taskstatustask":
                taskProducer.sendMessage(task, KAFKA_UPDATED_TASKSTATUSTASK);
                break;
            default:
                break;
        }

        return task;
    }

}
