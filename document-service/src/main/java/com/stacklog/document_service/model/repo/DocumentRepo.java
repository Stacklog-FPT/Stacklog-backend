package com.stacklog.document_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stacklog.document_service.model.entities.Document;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface DocumentRepo extends JpaRepository<Document, String> {

    Document findByDocumentId(String documentId);


    @Query("SELECT da.document FROM DocumentAccess da WHERE da.documentAccessBy = :userId OR da.document.createdBy = :userId")
    public List<Document> findByUserId(@Param("userId") String userId);
    
}
