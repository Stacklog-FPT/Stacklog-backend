package com.stacklog.task_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.service.TaskService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping(path = "/task")
public class TaskRestController {
    
    @Autowired TaskService taskService;

    @MessageMapping("/taskify")
    @SendTo("/topic/taskservice")
    public ResponseEntity<Map<String, String>> sendMessage(Map<String, String> message){
        // System.out.println("oke");
        return ResponseEntity.ok().body(message);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<List<Task>> getTasksByGroupId(@RequestHeader("Authorization") String token, @PathVariable("groupId") String groupId) {
        List<Task> lists = taskService.getAllByGroupId(token, groupId);
        if (lists.isEmpty() || lists == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(lists);
    }
    
    @PostMapping("")
    public ResponseEntity<Task> saveTask(@RequestHeader("Authorization") String token, @RequestBody Task e) {
        Task task = taskService.save(e, token);
        if (task == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(task);
    }
    
    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@RequestHeader("Authorization") String token, @PathVariable("taskId") String taskId) {
        Task task = taskService.delete(taskId, token);
        if (task == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body("Delete success");
    }

}
