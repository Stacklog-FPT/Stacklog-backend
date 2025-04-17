package com.stacklog.task_service.model.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.repo.TaskRepo;
// import com.stacklog.task_service.utils.kafka.KafkaService;
import com.stacklog.task_service.utils.redis.RedisService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskService implements IService<Task> {

    RedisService<Task> redisTaskService;

    public TaskService(RedisService<Task> redisTaskService) {
        this.redisTaskService = redisTaskService;
    }

    // @Autowired
    // KafkaService kafkaService;

    @Autowired
    TaskRepo taskRepo;

    @Override
    public List<Task> getAll() {
        List<Task> tasks = redisTaskService.getDatas();
        
        if (!tasks.isEmpty()) {
            log.info("✅ Loaded {} tasks from Redis cache", tasks.size());
        } else {
            tasks = taskRepo.findAll();
            redisTaskService.saveListToRedis(tasks);
            
        }
        return tasks;
    }

    @Override
    public Task getById(Long id) {
        Task task = redisTaskService.getDataById(id.toString());
        if (task != null) {
            log.info("✅ Loaded task from Redis cache", task.toString());
        } else {
            task = taskRepo.findById(id).orElse(new Task());
            redisTaskService.saveToRedis(task, task.getTaskId().toString(), "PENDING_WRITE");
        }
        return task;
    }

    @Override
    public Task save(Task e) {
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisTaskService.getCurrentUserId());

        if (e.getTaskId() == null || !taskRepo.findById(e.getTaskId()).isPresent()) {
            Long eId = UUID.randomUUID().timestamp();
            e.setTaskId(eId);
            e.setCreatedAt(CURRENT_TIME);
            e.setCreatedBy(redisTaskService.getCurrentUserId());
        }
        return redisTaskService.saveToRedis(e, e.getTaskId().toString(), "PENDING_WRITE");

    }

    @Override
    public Task remove(Long id) {
        return null;
    }

    public List<Task> getByGroupId(String groupId) {
        return getAll().stream()
                        .filter(task -> task.getGroupId().equals(groupId))
                        .toList();
    }

}
