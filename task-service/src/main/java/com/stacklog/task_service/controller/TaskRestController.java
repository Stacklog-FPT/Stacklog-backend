package com.stacklog.task_service.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.task_service.model.entities.CheckList;
import com.stacklog.task_service.model.entities.Review;
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
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(path = "/task")
public class TaskRestController {

    @Autowired
    TaskService taskService;

    @Autowired
    TaskAssignService taskAssignService;

    @Autowired
    StatusTaskService statusTaskService;

    @GetMapping("/{groupId}")
    public ResponseEntity<List<ResponseTask>> getTasksByGroupId(@RequestHeader("Authorization") String token,
            @PathVariable("groupId") String groupId) {
        List<ResponseTask> lists = new ArrayList<>();
        taskService.getAllByGroupId(token, groupId).stream().forEach(t -> lists.add(new ResponseTask(t)));

        if (lists.isEmpty() || lists == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(lists);
    }

    @GetMapping("/personal-task")
    public ResponseEntity<Map<String, List<ResponseTask>>> getTasksPersonal(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "semesterId", required = false) String semesterId) {

        List<Task> tasks = (semesterId != null && !semesterId.isBlank())
                ? taskService.getAllByUserIdAndSemesterId(token, semesterId)
                : taskService.getAllByUserId(token);

        Map<String, List<ResponseTask>> result = tasks.stream()
                .collect(Collectors.groupingBy(
                        t -> Optional.ofNullable(t.getStatusTask())
                                .map(st -> st.getStatusTaskName())
                                .map(String::trim)
                                .map(String::toUpperCase)
                                .orElse("UNKNOWN"),
                        LinkedHashMap::new,
                        Collectors.mapping(ResponseTask::new, Collectors.toList())));

        return ResponseEntity.ok(result);
    }

    @PostMapping("/save")
    public ResponseEntity<Task> saveTask(@RequestHeader("Authorization") String token, @RequestBody TaskDTO e) {
        Task task = new Task();
        if (e.getTaskId() != null || e.getTaskId().isBlank()) {
            task.setTaskId(e.getTaskId());
        }
        task.setTaskTitle(e.getTaskTitle());
        task.setTaskDescription(e.getTaskDescription());
        task.setGroupId(e.getGroupId());
        task.setDocumentId(e.getDocumentId());
        task.setTaskPoint(e.getTaskPoint());
        task.setTaskStartTime(e.getTaskStartTime());
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
    private String taskId;
    private String taskTitle;
    private String taskDescription;
    private String groupId;
    private String documentId;
    private Integer taskPoint;
    private LocalDateTime taskStartTime;
    private LocalDateTime taskDueDate;
    private Priority priority;
    private String statusTaskId;
    private List<String> listUserAssign;
    private String parentTaskId;
}

@Getter
@Setter
class ResponseTask {
    private String taskId;
    private String taskTitle;
    private String taskDescription;
    private String groupId;
    private String documentId;
    private Integer taskPoint;
    private LocalDateTime taskStartTime;
    private LocalDateTime taskDueDate;
    private Priority priority;
    private List<Task> subtasks;
    private List<Review> reviews;
    private String statusTaskId;
    private List<CheckList> checkLists;
    private List<String> assignTo;

    public ResponseTask(Task task) {
        this.taskId = task.getTaskId();
        this.taskTitle = task.getTaskTitle();
        this.taskDescription = task.getTaskDescription();
        this.groupId = task.getGroupId();
        this.documentId = task.getDocumentId();
        this.taskPoint = task.getTaskPoint();
        this.taskStartTime = task.getTaskStartTime();
        this.taskDueDate = task.getTaskDueDate();
        this.priority = task.getPriority();
        this.subtasks = task.getSubtasks();
        this.reviews = task.getReviews();
        this.statusTaskId = task.getStatusTask().getStatusTaskId();
        this.checkLists = task.getCheckLists();
        this.assignTo = convertAssignsToAssignTo(task.getAssigns());
    }

    private List<String> convertAssignsToAssignTo(List<TaskAssign> taskAssigns) {
        List<String> assignTo = new ArrayList<>();
        taskAssigns.stream().forEach(ta -> assignTo.add(ta.getAssignTo()));
        return assignTo;
    }

}
