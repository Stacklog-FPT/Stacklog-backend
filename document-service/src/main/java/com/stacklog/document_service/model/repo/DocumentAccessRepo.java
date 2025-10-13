package com.stacklog.document_service.model.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stacklog.document_service.model.entities.DocumentAccess;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface DocumentAccessRepo extends JpaRepository<DocumentAccess, String> {

    @Modifying
    @Query("delete from DocumentAccess da where da.document.documentId = :documentId")
    void deleteAllByDocumentId(String documentId);

    @Modifying
    @Query("delete from DocumentAccess da where da.document.documentId = :documentId and da.documentAccessBy not in :keepIds")
    void deleteAllNotIn(String documentId, Set<String> keepIds);

    DocumentAccess findByDocumentDocumentIdAndDocumentAccessBy(@Param("documentId") String documentId,
            @Param("documentAccessBy") String documentAccessBy);

}
