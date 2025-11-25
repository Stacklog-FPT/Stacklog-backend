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
            and st.groupId = :groupId
            """)
    List<ScoreItem> findScoreItemByUserIds(String groupId, List<String> userIds);

    @Query("""
                select si
                from ScoreItem si
                join ScoreCategory sc on sc.scoreCategoryId = si.scoreCategory.scoreCategoryId
                where si.userId = :userId
                and sc.classId = :classId
            """)
    List<ScoreItem> findAllByUserIdNClassId(String userId, String classId);

}
