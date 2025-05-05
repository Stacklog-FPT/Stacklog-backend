package com.stacklog.class_service.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stacklog.class_service.model.entities.GroupStudent;

@Repository
public interface GroupsStudentRepo extends JpaRepository<GroupStudent, String> {
    
}
