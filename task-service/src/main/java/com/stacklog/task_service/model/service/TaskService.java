package com.stacklog.task_service.model.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.repo.TaskRepo;
import com.stacklog.task_service.utils.kafka.KafkaService;
import com.stacklog.task_service.utils.redis.RedisService;

@Service
public class TaskService implements IService<Task> {

    RedisService<Task> redisTaskService;
    
    public TaskService(RedisService<Task> redisTaskService) {
        this.redisTaskService = redisTaskService;
    }

    @Autowired
    KafkaService kafkaService;

    @Autowired
    TaskRepo taskRepo;

    

    @Override
    public List<Task> getAll() {
        return taskRepo.findAll();
    }

    public List<Task> getAllByGroupId(String groupId) {
        return taskRepo.getAllByGroupId(groupId).stream()
                .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
                .collect(Collectors.toList());
    }

    @Override
    public Task getById(Long id) {
        return taskRepo.getReferenceById(id);
    }

    @Override
    public Task save(Task e) {
        e.setCreatedAt(CURRENT_TIME);
        e.setUpdateAt(CURRENT_TIME);
        e.setCreatedBy();
        // return new Task();
        return taskRepo.save(e);
    }

    @Override
    public Task remove(Long id) {
        Task task = getById(id);
        if (task != null) {
            taskRepo.deleteById(id);
            return task;
        }
        return null;

    }

}
