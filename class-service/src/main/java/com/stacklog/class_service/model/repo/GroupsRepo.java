package com.stacklog.class_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stacklog.class_service.model.entities.Groupss;

@Repository
public interface GroupsRepo extends JpaRepository<Groupss, String> {

    @Query("SELECT gs.groups FROM GroupStudent gs WHERE gs.userId = :userId")
    List<Groupss> findByUserId(@Param("userId") String userId);

    List<Groupss> findByClassesClassesId(String classesId);

}
