package com.stacklog.task_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stacklog.task_service.model.entities.Task;

@Repository
public interface TaskRepo extends JpaRepository<Task, String> {

    public List<Task> getAllByGroupId(String groupId);

    @Query("SELECT ta.task FROM TaskAssign ta WHERE ta.assignTo = :userId")
    public List<Task> findByUserId(@Param("userId") String userId);

    @Query("""
              select distinct t
              from Task t
              left join fetch t.statusTask st
              left join fetch t.subtasks s
              where t.groupId = :groupId
                and t.parentTask is null
            """)
    public List<Task> findByGroupId(@Param("groupId") String groupId);

}
