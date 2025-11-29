package com.stacklog.score_service.model.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stacklog.score_service.model.entities.ScoreCategory;

@Repository
public interface ScoreCategoryRepo extends JpaRepository<ScoreCategory, String> {

    @Query("select sc from ScoreCategory sc where sc.classId = :classId")
    List<ScoreCategory> findAllByClassId(String classId);

    @Query("select sc from ScoreCategory sc where sc.isReusable = true")
    List<ScoreCategory> findAllByIsReusable(boolean isReuse);

    // @Query("select sc from ScoreCategory sc where sc.scoreCategoryName = :scoreCategoryName and sc.classId = :groupId")
    // Optional<ScoreCategory> findByScoreCategoryNameAndClassId(String scoreCategoryName, String classId);
    
}
