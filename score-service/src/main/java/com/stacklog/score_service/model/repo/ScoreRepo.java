package com.stacklog.score_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stacklog.score_service.model.entities.Score;

@Repository
public interface ScoreRepo extends JpaRepository<Score, String> {

    List<Score> findByUserId(String currentUserId);
    
}
