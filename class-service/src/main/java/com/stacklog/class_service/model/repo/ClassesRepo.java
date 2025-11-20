package com.stacklog.class_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stacklog.class_service.model.entities.Classes;

@Repository
public interface ClassesRepo extends JpaRepository<Classes, String> {

  @Query("""
        SELECT DISTINCT c
        FROM GroupStudent gs
        JOIN gs.groups g
        JOIN g.classes c
        JOIN c.semester s
        WHERE gs.userId = :currentUserId
          AND s.semesterId = :semesterId
      """)
  List<Classes> findAllBySemesterIdNUserId(
      @Param("currentUserId") String currentUserId,
      @Param("semesterId") String semesterId);

  List<Classes> findAllByLectureIdAndSemesterSemesterId(String lectureId, String semesterId);

<<<<<<< HEAD
  @Query("SELECT c FROM Classes c JOIN c.semester s WHERE s.semesterId = :semesterId")
  List<Classes> findAllBySemesterSemesterId(@Param("semesterId") String semesterId);

=======
>>>>>>> 87f5ba462fa3f589fb08e5b6fa8ea5daec56a383
}
