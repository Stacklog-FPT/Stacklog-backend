package com.stacklog.task_service.model.entities;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private Long taskId;

    private String taskTitle;
    private String taskDescription;
    private String groupId;
    private String documentId;
    private Integer taskPoint;
    private LocalDateTime taskDueDate;

    @Enumerated(EnumType.STRING) // hoặc EnumType.ORDINAL
    private Priority priority;

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    @ManyToOne
    @JoinColumn(name = "statusTaskId")
    private StatusTask statusTask;

    @ManyToOne
    @JoinColumn(name = "parentTaskId")
    private Task parentTask;

    @OneToMany(mappedBy = "parentTask")
    @JsonIgnore
    private List<Task> subtasks;

    @OneToMany(mappedBy = "task")
    private List<TaskAssign> assigns;

    public Task(String createdBy, String createdAt, String updateBy, String updateAt, String taskTitle,
            String taskDescription, String groupId, String documentId, Integer taskPoint, String taskDueDate,
            StatusTask statusTask, Task parentTask, List<Task> subtasks, List<TaskAssign> assigns) {
        super(createdBy, createdAt, updateBy, updateAt);
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.groupId = groupId;
        this.documentId = documentId;
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

    public Task() {
    }
    
    

}
