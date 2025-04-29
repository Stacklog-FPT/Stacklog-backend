package com.stacklog.task_service.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stacklog.task_service.dto.TaskRequest;
import com.stacklog.task_service.model.entities.CheckList;
import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.service.CheckItemService;
import com.stacklog.task_service.model.service.CheckListService;
import com.stacklog.task_service.model.service.StatusTaskService;
import com.stacklog.task_service.model.service.TaskAssignService;
import com.stacklog.task_service.model.service.TaskService;

@Component
public class TaskMapper {

    @Autowired
    CheckListService checkListService;

    @Autowired
    TaskAssignService taskAssignService;

    @Autowired
    CheckItemService checkItemService;

    @Autowired
    StatusTaskService statusTaskService;

    @Autowired TaskService taskService;

    @Autowired
    TaskAssignMapper taskAssignMapper;

    public Task toEntity(TaskRequest taskRequest) {
        Task task = new Task();
        task.setTaskTitle(taskRequest.getTaskTitle());
        task.setTaskDescription(taskRequest.getTaskDescription());
        task.setGroupId(taskRequest.getGroupId());
        task.setDocumentId(taskRequest.getDocumentId());
        task.setTaskPoint(taskRequest.getTaskPoint());
        task.setTaskDueDate(taskRequest.getTaskDueDate());

        task.setPriority(taskRequest.getPriority() != null ? taskRequest.getPriority() : Task.Priority.LOW);

        task.setAssigns(taskAssignMapper.generateAssigns(taskRequest.getTaskId(), taskRequest.getAssignIds()));
        
        return task;

    }

    

}
