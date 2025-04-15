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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping(path = {""})
public class RestTaskController {

    @Autowired
    TaskService taskService;
    
    @GetMapping("")
    public ResponseEntity<List<Task>> getAllTask() {
        List<Task> tasks = taskService.getAll();
        return new ResponseEntity<List<Task>>(tasks, HttpStatus.OK);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<List<Task>> getAllTask(@PathVariable(name = "groupId") String groupId) {
        List<Task> tasks = taskService.getAllByGroupId(groupId);
        return new ResponseEntity<List<Task>>(tasks, HttpStatus.OK);
    }
    
    @PostMapping("")
    public ResponseEntity<Task> save() {
        taskService.save(new Task());
        return new ResponseEntity<Task>(new Task(), HttpStatus.OK);
    }
    
    

}
