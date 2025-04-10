package com.stacklog.task_service.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.repo.TaskRepo;

@Service
public class TaskService implements IService<Task> {

    @Autowired
    TaskRepo taskRepo;

    @Override
    public List<Task> getAll() {
        return taskRepo.findAll();
    }

    @Override
    public Task getById(String id) {
        return taskRepo.getReferenceById(id);
    }

    @Override
    public Task save(Task e) {
        e.setCreatedAt(CURRENT_TIME);
        e.setUpdateAt(CURRENT_TIME);
        return taskRepo.save(e);
    }

    @Override
    public Task remove(String id) {
        Task task = getById(id);
        if (task != null) {
            taskRepo.deleteById(id);
            return task;
        }
        return null;
        
    }

    
    
}
