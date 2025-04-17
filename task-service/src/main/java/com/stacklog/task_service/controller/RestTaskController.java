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
@RequestMapping(path = { "" })
public class RestTaskController {

    @Autowired
    TaskService taskService;

    @GetMapping("/{groupId}")
    public ResponseEntity<List<Task>> getAllTask(@PathVariable(name = "groupId") String groupId) {
        try {
            List<Task> tasks = taskService.getByGroupId(groupId);
            return new ResponseEntity<List<Task>>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return ResponseEntity.badRequest().build();

    }

    @PostMapping("")
    public ResponseEntity<Task> save(@RequestBody Task task) {
        try {
            Task newTask = taskService.save(task);
            return new ResponseEntity<Task>(newTask, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();

    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Task> delete(@PathVariable(name = "taskId") String taskId) {
        try {
            taskService.remove(Long.parseLong(taskId));
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

}

// class TaskDTO {
// private Long statusTaskId;
// private String groupId;
// private String

// }
