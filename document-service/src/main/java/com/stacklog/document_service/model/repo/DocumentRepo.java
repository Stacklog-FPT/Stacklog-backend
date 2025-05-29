package com.stacklog.document_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stacklog.document_service.model.entities.Document;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface DocumentRepo extends JpaRepository<Document, String> {

    @Query(value = "SELECT d FROM Document d WHERE d.create_by = :userId", nativeQuery = true)
    List<Document> findByUserId(@Param("userId") String userId);
    
}
