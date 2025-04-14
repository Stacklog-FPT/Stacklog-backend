package com.stacklog.task_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.service.TaskService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping(path = {"/api/task"})
public class RestTaskController {

    @Autowired
    TaskService taskService;
    
    @GetMapping("")
    public ResponseEntity<List<Task>> getAllTask() {
        List<Task> tasks = taskService.getAll().stream().sorted().toList();
        return new ResponseEntity<List<Task>>(tasks, HttpStatus.OK);
    }
    

}
