package com.stacklog.task_service.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.TaskAssign;
import com.stacklog.task_service.model.repo.TaskAssignRepo;
import com.stacklog.task_service.utils.redis.RedisService;

@Service
public class TaskAssignService implements IService<TaskAssign> {

    RedisService<TaskAssign> redisTaskAssignService;

    @Autowired
    TaskAssignRepo taskAssignRepo;

    @Override
    public List<TaskAssign> getAll() {
        return taskAssignRepo.findAll();
    }

    @Override
    public TaskAssign getById(String id) {
        return taskAssignRepo.findById(id).orElseThrow();
    }

    @Override
    public TaskAssign remove(String id) {
        TaskAssign taskAssign = getById(id);
        taskAssignRepo.deleteById(id);
        return taskAssign;
    }

    @Override
    public TaskAssign save(TaskAssign e) {
        return taskAssignRepo.save(e);
    }

    public List<TaskAssign> getByTaskId(String taskId) {
        return getAll().stream().filter((TaskAssign taskAssign) -> taskAssign.getTask().getTaskId().equals(taskId))
                .toList();
    }

}
