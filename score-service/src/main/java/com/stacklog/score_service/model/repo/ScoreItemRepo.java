package com.stacklog.score_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stacklog.score_service.model.entities.ScoreItem;

@Repository
public interface ScoreItemRepo extends JpaRepository<ScoreItem, String> {

    List<ScoreItem> findAllByUserId(String userId);

    @Query("""
            select distinct st
            from ScoreItem st
            where st.userId in :userIds
            """)
    List<ScoreItem> findScoreItemByUserIds(List<String> userIds);
    
}
