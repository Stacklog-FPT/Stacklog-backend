package com.stacklog.task_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.task_service.model.entities.StatusTask;
import com.stacklog.task_service.model.service.StatusTaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping(path = "/statusTask")
public class RestStatusTaskController {

    @Autowired
    StatusTaskService statusTaskService;

    @MessageMapping("/statustaskify")
    @SendTo("/topic/statustaskify")
    public ResponseEntity<StatusTask> sendMessage(StatusTask statusTask){
        return ResponseEntity.ok().body(statusTask);
    }

    @PutMapping("")
    public ResponseEntity<StatusTask> updateStatusTask(@RequestBody StatusTask statusTask) {
        statusTaskService.save(statusTask);
        statusTaskService.sendNotification(statusTask, "statustask");
        return ResponseEntity.ok().body(statusTask);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<List<StatusTask>> getStatusTask(@PathVariable(name = "groupId") String groupId) {
        return ResponseEntity.ok().body(statusTaskService.getByGroupId(groupId));
    }
    

}
