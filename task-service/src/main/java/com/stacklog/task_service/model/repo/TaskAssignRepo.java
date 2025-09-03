package com.stacklog.task_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stacklog.task_service.model.entities.TaskAssign;


@Repository
public interface TaskAssignRepo extends JpaRepository<TaskAssign, String> {
    
    public List<TaskAssign> findByAssignTo(String assignTo);

    public List<TaskAssign> findByTaskTaskId(String taskId);

    boolean existsByTaskTaskIdAndAssignTo(String taskId, String assignTo);

}
