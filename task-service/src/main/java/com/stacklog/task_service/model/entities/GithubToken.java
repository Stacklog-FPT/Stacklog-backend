package com.stacklog.task_service.model.entities;

import com.stacklog.core_service.model.entities.CoreEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "github_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GithubToken extends CoreEntity {
    
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String groupId;

    private String accessToken;

}
