package com.stacklog.task_service.model.entities;

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
public class Review extends CoreEntity  {
    
    @Id
    private String reviewId;

    private String review

}
