package com.stacklog.task_service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.task_service.model.service.TaskService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(path = "/auto-check-deadline")
public class AutoCheckDeadlineByHour {
  
  @Autowired
  TaskService taskService;

  @PostMapping("")
  public ResponseEntity<?> checkDeadlineTask(@RequestBody Map<String, String> mapEmailById) {
    if (mapEmailById == null) {
      return ResponseEntity.status(212).body("No body");
    }
    String message = taskService.sendEmailToAssignUser(mapEmailById);
      
    return ResponseEntity.ok(message);
  }
  

}
