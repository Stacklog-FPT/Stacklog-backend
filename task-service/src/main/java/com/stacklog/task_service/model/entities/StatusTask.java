package com.stacklog.task_service.model.entities;

import jakarta.persistence.Entity;
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
    private String statusTaskId;

    private String statusTaskName;
    private String statusTaskColor;
    public StatusTask(String createdBy, String createdAt, String updateBy, String updateAt, String statusTaskId,
            String statusTaskName, String statusTaskColor) {
        super(createdBy, createdAt, updateBy, updateAt);
        this.statusTaskId = statusTaskId;
        this.statusTaskName = statusTaskName;
        this.statusTaskColor = statusTaskColor;
    }

    

}
