package com.stacklog.class_service.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stacklog.class_service.model.entities.Classes;

@Repository
public interface ClassesRepo extends JpaRepository<Classes, String> {
    
}
