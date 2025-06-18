package com.stacklog.score_service.model.entities;

import com.stacklog.core_service.model.entities.CoreEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Score extends CoreEntity {
    
    @Id
    private String scoreId;

    private Double scorePoint;
    private String scoreName;
    private Double scorePercentage;

    private String userId;

    private String classId;

}
