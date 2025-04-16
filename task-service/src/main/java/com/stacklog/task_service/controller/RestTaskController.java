package com.stacklog.task_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.service.TaskService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping(path = {""})
public class RestTaskController {

    @Autowired
    TaskService taskService;
    
    // @GetMapping("")
    // public ResponseEntity<List<Task>> getAllTask() {
    //     List<Task> tasks = taskService.getAll();
    //     return new ResponseEntity<List<Task>>(tasks, HttpStatus.OK);
    // }

    @GetMapping("/{groupId}")
    public ResponseEntity<List<Task>> getAllTask(@PathVariable(name = "groupId") String groupId) {
        List<Task> tasks = taskService.getAllByGroupId(groupId);
        return new ResponseEntity<List<Task>>(tasks, HttpStatus.OK);
    }
    
    @PostMapping("")
    public ResponseEntity<Task> save(@RequestBody()) {
        boolean result = taskService.save(new Task()) == null ? true:false;
        return new ResponseEntity<Task>(new Task(), HttpStatus.OK);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Task> delete(@PathVariable(name = "taskId") String taskId ) {
        taskService.remove(Long.parseLong(taskId));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    
    

}

class TaskDTO {
    private Long statusTaskId;
    private 
    
}
