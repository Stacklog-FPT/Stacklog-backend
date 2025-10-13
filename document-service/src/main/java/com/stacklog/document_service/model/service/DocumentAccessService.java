package com.stacklog.document_service.model.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.document_service.model.entities.DocumentAccess;
import com.stacklog.document_service.model.repo.DocumentAccessRepo;

import jakarta.transaction.Transactional;

@Service
public class DocumentAccessService{

    private final RedisService<DocumentAccess> redisDocumentAccessService;

    public DocumentAccessService (RedisService<DocumentAccess> redisService) {
        this.redisDocumentAccessService = redisService;
    }

    @Autowired
    private DocumentAccessRepo documentAccessRepo;
    
    public void deleteDocumentAccess(String documentId, List<DocumentAccess> assignsFE) {
        Set<String> keepIds = assignsFE.stream()
                .map(DocumentAccess::getDocumentAccessId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (keepIds == null || keepIds.isEmpty()) {
            documentAccessRepo.deleteAllByDocumentId(documentId);
            return;
        } 
        documentAccessRepo.deleteAllNotIn(documentId, keepIds);
    }

    @Transactional
    public DocumentAccess save(DocumentAccess e, String token) {
        DocumentAccess documentAccess = documentAccessRepo.findByDocumentDocumentIdAndDocumentAccessBy(e.getDocument().getDocumentId(), e.getDocumentAccessBy());
        boolean isCreate = (documentAccess == null);
        e.setUpdateAt(CommonFunction.getCurrentTime());
        e.setUpdateBy(redisDocumentAccessService.getCurrentUserId(token));
        if (isCreate) {
            e.setCreatedAt(CommonFunction.getCurrentTime());
            e.setCreatedBy(redisDocumentAccessService.getCurrentUserId(token));
            e.setDocumentAccessId(UUID.randomUUID().toString());
        } else {
            e.setDocumentAccessId(documentAccess.getDocumentAccessId());
        }

        e = documentAccessRepo.save(e);

        return e;
    }

}
