package com.stacklog.topic_service.model.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stacklog.topic_service.model.entities.ProjectInformation;

@Repository
public interface ProjectInformationRepo extends JpaRepository<ProjectInformation, String> {

    List<ProjectInformation> findAllByUserId(String currentUserId);

    Optional<ProjectInformation> findByGroupId(String groupId);

    @Query("""
            select distinct t
            from ProjectInformation pi
            WHERE t.groupId in :groupIds
            """)
    List<ProjectInformation> findAllByGroupIds(@Param("userId") String userId,
            @Param("groupIds") List<String> groupIds);

}
