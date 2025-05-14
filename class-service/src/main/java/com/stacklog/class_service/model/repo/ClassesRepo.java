package com.stacklog.class_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stacklog.class_service.model.entities.Classes;

@Repository
public interface ClassesRepo extends JpaRepository<Classes, String> {

    @Query("SELECT DISTINCT gs.groups.classes FROM GroupStudent gs WHERE gs.userId = :userId")
    List<Classes> findByUserId(@Param("userId") String userId);

}
