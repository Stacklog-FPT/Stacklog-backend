package com.stacklog.task_service.model.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Task extends CoreEntity {

    @Id
    private String taskId;

    private String taskTitle;
    private String taskDescription;
    private String groupId;
    private String doucumentId;
    private Integer taskPoint;
    private LocalDateTime taskDueDate;

    public enum priority {
        HIGH, MEDIUM, LOW
    }

    @ManyToOne
    @JoinColumn(name = "statusTaskId")
    private StatusTask statusTask;

    @ManyToOne
    @JoinColumn(name = "parentTaskId")
    private Task parentTask;

    @OneToMany(mappedBy = "parentTask")
    private List<Task> subtasks;

    @OneToMany(mappedBy = "task")
    private List<TaskAssign> assigns;

    public Task(String createdBy, String createdAt, String updateBy, String updateAt, String taskId, String taskTitle,
            String taskDescription, String groupId, String doucumentId, Integer taskPoint, String taskDueDate,
            StatusTask statusTask, Task parentTask, List<Task> subtasks, List<TaskAssign> assigns) {
        super(createdBy, createdAt, updateBy, updateAt);
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.groupId = groupId;
        this.doucumentId = doucumentId;
        this.taskPoint = taskPoint;
        this.statusTask = statusTask;
        this.parentTask = parentTask;
        this.subtasks = subtasks;
        this.assigns = assigns;
        try {
            this.taskDueDate = super.convertTime(taskDueDate);
        } catch (Exception e) {
            System.out.println(e);
            this.taskDueDate = null;
        }

    }
    
    

}
