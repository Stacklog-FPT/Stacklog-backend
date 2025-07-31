package com.stacklog.task_service.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.entities.TaskAssign;
import com.stacklog.task_service.model.entities.Task.Priority;
import com.stacklog.task_service.model.service.StatusTaskService;
import com.stacklog.task_service.model.service.TaskAssignService;
import com.stacklog.task_service.model.service.TaskService;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(path = "/task")
public class TaskRestController {

    @Autowired
    TaskService taskService;

    @Autowired
    TaskAssignService taskAssignService;

    @Autowired
    StatusTaskService statusTaskService;

    @MessageMapping("/taskify")
    @SendTo("/topic/taskservice")
    public ResponseEntity<Map<String, String>> sendMessage(Map<String, String> message) {
        // System.out.println("oke");
        return ResponseEntity.ok().body(message);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<List<Task>> getTasksByGroupId(@RequestHeader("Authorization") String token,
            @PathVariable("groupId") String groupId) {
        List<Task> lists = taskService.getAllByGroupId(token, groupId);
        if (lists.isEmpty() || lists == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(lists);
    }

    @PostMapping("")
    public ResponseEntity<Task> saveTask(@RequestHeader("Authorization") String token, @RequestBody TaskDTO e) {
        Task task = new Task();
        task.setTaskTitle(e.getTaskTitle());
        task.setTaskDescription(e.getTaskDescription());
        task.setGroupId(e.getGroupId());
        task.setDocumentId(e.getDocumentId());
        task.setTaskPoint(e.getTaskPoint());
        task.setTaskDueDate(e.getTaskDueDate());
        task.setPriority(e.getPriority());
        task.setStatusTask(statusTaskService.getById(e.getStatusTaskId(), token));
        if (e.getParentTaskId() != null && !e.getParentTaskId().isBlank()) {
            Task parent = taskService.getById(e.getParentTaskId(), token);
            task.setParentTask(parent);
        } else {
            task.setParentTask(null);
        }
        task = taskService.save(task, token);
        for (String userId : e.getListUserAssign()) {
            TaskAssign taskAssign = new TaskAssign();
            taskAssign.setAssignTo(userId);
            taskAssign.setTask(task);
            taskAssignService.save(taskAssign, token);
        }
        if (task == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(task);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@RequestHeader("Authorization") String token,
            @PathVariable("taskId") String taskId) {
        Task task = taskService.delete(taskId, token);
        if (task == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body("Delete success");
    }

}

@Getter
@Setter
class TaskDTO {
    private String taskTitle;
    private String taskDescription;
    private String groupId;
    private String documentId;
    private Integer taskPoint;
    private LocalDateTime taskDueDate;
    private Priority priority;
    private String statusTaskId;
    private List<String> listUserAssign;
    private String parentTaskId;
}
