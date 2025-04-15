package com.stacklog.task_service.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class StatusTask extends CoreEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long statusTaskId;

    private String statusTaskName;
    private String statusTaskColor;
    private String groupId;

    public StatusTask(String createdBy, String createdAt, String updateBy, String updateAt,
            String statusTaskName, String statusTaskColor) {
        super(createdBy, createdAt, updateBy, updateAt);
        this.statusTaskName = statusTaskName;
        this.statusTaskColor = statusTaskColor;
    }

    public StatusTask() {
    }

    

}
