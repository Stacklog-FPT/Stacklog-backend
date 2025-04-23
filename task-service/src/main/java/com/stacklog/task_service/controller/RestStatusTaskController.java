package com.stacklog.task_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.task_service.model.entities.StatusTask;
import com.stacklog.task_service.model.service.StatusTaskService;

@RestController
public class RestStatusTaskController {

    @Autowired
    StatusTaskService statusTaskService;

    @MessageMapping("/statustaskify")
    @SendTo("/topic/statustaskify")
    public ResponseEntity<StatusTask> sendMessage(StatusTask statusTask){
        return ResponseEntity.ok().body(statusTask);
    }

    @PutMapping("/statusTask")
    public ResponseEntity<StatusTask> updateStatusTask(@RequestBody StatusTask statusTask) {
        statusTaskService.save(statusTask);
        statusTaskService.sendNotification(statusTask, "statustask");
        return ResponseEntity.ok().body(statusTask);
    }

}
