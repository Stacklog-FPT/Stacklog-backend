package com.stacklog.task_service.model.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stacklog.task_service.model.entities.GithubToken;

@Repository
public interface GithubTokenRepo extends JpaRepository<GithubToken, String> {
    
    Optional<GithubToken> findByGroupId(String groupId);

}
