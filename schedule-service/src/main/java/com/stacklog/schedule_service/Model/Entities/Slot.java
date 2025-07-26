package com.stacklog.schedule_service.model.entities;

import java.time.LocalDateTime;

import com.stacklog.core_service.model.entities.CoreEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Slot extends CoreEntity {
    
    @Id
    private String slotId;

    private String slotTitle;
    private String slotDescription;
    private LocalDateTime slotStarTime;

    private String groupId;

}