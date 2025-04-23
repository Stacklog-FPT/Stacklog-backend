package com.stacklog.task_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.task_service.model.entities.CheckItem;
import com.stacklog.task_service.model.entities.CheckList;
import com.stacklog.task_service.model.entities.StatusTask;
import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.entities.TaskAssign;
import com.stacklog.task_service.model.service.CheckItemService;
import com.stacklog.task_service.model.service.CheckListService;
import com.stacklog.task_service.model.service.StatusTaskService;
import com.stacklog.task_service.model.service.TaskAssignService;
import com.stacklog.task_service.model.service.TaskService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping(path = { "" })
public class RestTaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    CheckListService checkListService;

    @Autowired
    TaskAssignService taskAssignService;

    @Autowired
    CheckItemService checkItemService;

    @Autowired
    StatusTaskService statusTaskService;

    @MessageMapping("/taskify")
    @SendTo("/topic/task")
    public ResponseEntity<Task> sendMessage(Task task){
        return ResponseEntity.ok().body(task);
    }

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

    @PutMapping("")
    public ResponseEntity<Task> saveUpdateTask(@RequestBody Task task) {
        try {
            System.out.println(task.toString());
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
            taskService.remove(taskId);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    // @PutMapping("/{taskId}/checklist")
    // public ResponseEntity<Task> updateChecklist(@PathVariable(name = "taskId") String taskId, @RequestBody CheckList checkList) {
    //     checkListService.save(checkList);
    //     Task task = taskService.sendNotification(taskService.getById(Long.parseLong(taskId)), "checklist");
    //     return ResponseEntity.ok().body(task);
    // }

    // @PutMapping("/{taskId}/checkItem")
    // public ResponseEntity<Task> putMethodName(@PathVariable(name = "taskId") String taskId, @RequestBody CheckItem checkItem) {
    //     checkItemService.save(checkItem);
    //     Task task = taskService.sendNotification(taskService.getById(Long.parseLong(taskId)), "checkitem");
    //     return ResponseEntity.ok().body(task);
    // }

    // @PutMapping("/{taskId}/taskAssign")
    // public ResponseEntity<Task> updateAssign(@PathVariable(name = "taskId") String taskId, @RequestBody TaskAssign taskAssign) {
    //     taskAssignService.save(taskAssign);
    //     Task task = taskService.sendNotification(taskService.getById(Long.parseLong(taskId)), "taskassign");
    //     return ResponseEntity.ok().body(task);
    // }

    // @PutMapping("/{taskId}/statusTask")
    // public ResponseEntity<Task> updateStatusOfTask(@PathVariable(name = "taskId") String taskId, @RequestBody StatusTask statusTask) {
    //     Task task = taskService.getById(Long.parseLong(taskId));
    //     task.setStatusTask(statusTask);
    //     taskService.sendNotification(taskService.getById(Long.parseLong(taskId)), "taskstatustask");
    //     return ResponseEntity.ok().body(task);
    // }

    

}
