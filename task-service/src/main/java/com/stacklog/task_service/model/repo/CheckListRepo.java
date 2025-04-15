package com.stacklog.task_service.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stacklog.task_service.model.entities.CheckList;

@Repository
public interface CheckListRepo extends JpaRepository<CheckList, Long> {
    
}
