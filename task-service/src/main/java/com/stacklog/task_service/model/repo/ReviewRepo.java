package com.stacklog.task_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stacklog.task_service.model.entities.Review;

@Repository
public interface ReviewRepo extends JpaRepository<Review, String> {

    @Query("SELECT r FROM Review r WHERE r.task.taskId = :taskId")
List<Review> findByTaskId(@Param("taskId") String taskId);
    
}
