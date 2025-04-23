package com.stacklog.task_service.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.StatusTask;
import com.stacklog.task_service.model.repo.StatusTaskRepo;

@Service
public class StatusTaskService implements IService<StatusTask> {

    private final String KAFKA_UPDATED_STATUSTASK = "task-service.statustask.updated";

    @Autowired
    StatusTaskRepo statusTaskRepo;

    @Autowired
    TaskService taskService;

    @Override
    public List<StatusTask> getAll() {
        return statusTaskRepo.findAll();
    }

    @Override
    public StatusTask getById(Long id) {
        return statusTaskRepo.findById(id).orElseThrow();
    }

    @Override
    public StatusTask remove(Long id) {
        StatusTask statusTask = getById(id);
        statusTaskRepo.deleteById(id);
        return statusTask;
    }

    @Override
    public StatusTask save(StatusTask e) {
        return statusTaskRepo.save(e);
    }

    public StatusTask sendNotification(StatusTask statusTask, String topic) {
        statusTask.setUpdateAt(CURRENT_TIME);
        statusTask.setUpdateBy(taskService.redisTaskService.getCurrentUserId());
        taskService.taskProducer.sendMessage(statusTask, KAFKA_UPDATED_STATUSTASK);
        return statusTask;
    }

}
