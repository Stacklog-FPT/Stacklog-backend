package com.stacklog.task_service.model.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stacklog.task_service.model.entities.Task;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    
    public List<Optional<Task>> getAllByGroupId(String groupId);

}
