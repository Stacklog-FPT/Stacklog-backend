package com.stacklog.task_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stacklog.task_service.model.entities.TaskAssign;

@Repository
public interface TaskAssignRepo extends JpaRepository<TaskAssign, String> {

    public List<TaskAssign> findByAssignTo(String assignTo);

    public List<TaskAssign> findByTaskTaskId(String taskId);

    @Query(value = """
            SELECT EXISTS (
              SELECT 1
              FROM task_assign ta
              WHERE ta.task_id = :taskId
                AND ta.assign_to = :assignTo
            )
            """, nativeQuery = true)
    Long existsByTaskIdAndAssignTo(@Param("taskId") String taskId,
            @Param("assignTo") String assignTo);

}
