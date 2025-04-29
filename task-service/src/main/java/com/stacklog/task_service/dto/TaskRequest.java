package com.stacklog.task_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.entities.TaskAssign;
import com.stacklog.task_service.model.entities.Task.Priority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskRequest {

    private String taskId;
    private String taskTitle;
    private String taskDescription;
    private String documentId;
    private Integer taskPoint;
    private LocalDateTime taskDueDate;
    private Priority priority = Priority.LOW;

    private String groupId;
    private String statusTaskId;
    private String parentTaskId;
    private String checkListId;

    private String[] assignIds;

}
