package com.stacklog.score_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stacklog.score_service.model.entities.ScoreCategory;

@Repository
public interface ScoreCategoryRepo extends JpaRepository<ScoreCategory, String> {

    List<ScoreCategory> findAllByClassId(String classId);
    
}
