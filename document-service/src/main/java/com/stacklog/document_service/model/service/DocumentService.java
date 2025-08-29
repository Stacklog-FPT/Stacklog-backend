package com.stacklog.document_service.model.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.document_service.model.entities.Document;
import com.stacklog.document_service.model.repo.DocumentRepo;

@Service
public class DocumentService implements IService<Document> {

    private static final String NAME_SERVICE = "document-service";

    private static final String KAFKA_TOPIC_UPDATE = "document-service.document.updated";
    private static final String KAFKA_TOPIC_CREATE = "document-service.document.created";

    private static final String LOCATION_DIRECTORY = "Storage-Files";

    @Autowired
    DocumentRepo documentRepo;

    @Autowired
    KafkaProducer<Document> kafkaDocumentProducer;

    @Autowired
    RedisService<Document> redisDocumentService;

    public DocumentService(RedisService<Document> redisDocumentService) {
        this.redisDocumentService = redisDocumentService;
    }

    @Override
    public Document delete(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<Document> getAllByUserId(String token) {
        List<Document> documents = redisDocumentService.getAll(token, NAME_SERVICE);
        if (documents == null || documents.isEmpty()) {
            documents = documentRepo.findByUserId(redisDocumentService.getCurrentUserId(token));
            redisDocumentService.saveListToRedis(documents, token, NAME_SERVICE);
        }
        return documents;
    }

    @Override
    public Document getById(String id, String token) {
        Document document = redisDocumentService.getById(id, token, NAME_SERVICE);
        if (document == null) {
            document = documentRepo.findById(id).orElseThrow();
            redisDocumentService.saveToRedis(document, token, NAME_SERVICE);
        }
        return null;
    }

    @Override
    @Transactional
    public Document save(Document e, String token) {
        boolean isCreate = (e.getDocumentId() == null || !documentRepo.existsById(e.getDocumentId()));
        e.setUpdateAt(CommonFunction.getCurrentTime());
        e.setUpdateBy(redisDocumentService.getCurrentUserId(token));
        if (e.getDocumentId() == null) {
            e.setCreatedAt(CommonFunction.getCurrentTime());
            e.setCreatedBy(redisDocumentService.getCurrentUserId(token));
            e.setDocumentId(UUID.randomUUID().toString());
        }
        if (isCreate) {
            kafkaDocumentProducer.sendMessage(e, KAFKA_TOPIC_CREATE);
        } else {
            kafkaDocumentProducer.sendMessage(e, KAFKA_TOPIC_UPDATE);
        }

        redisDocumentService.saveToRedis(e, token, NAME_SERVICE);

        return e;
    }

    @Transactional
    public Document saveFile(MultipartFile file, Document d, String token) {
        File newFile = new File(LOCATION_DIRECTORY + File.separator + file.getOriginalFilename());
        try {
            Files.copy(file.getInputStream(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            String documentId = d.getDocumentId() == null ? d.getDocumentId() : null;

            Document document = new Document(documentId, file.getOriginalFilename(),
                    "/document-service/downloadFile/" + documentId, file.getContentType(), newFile.getPath(),
                    d.getDocumentLocations());
            return save(document, token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
