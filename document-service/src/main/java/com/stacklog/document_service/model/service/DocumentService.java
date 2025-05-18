package com.stacklog.document_service.model.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();
    
    @Autowired DocumentRepo documentRepo;

    @Autowired KafkaProducer<Document> kafkaDocumentProducer;

    @Autowired RedisService<Document> redisDocumentService;

    public DocumentService (RedisService<Document> redisDocumentService) {
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
        // Document document = redisDocumentService.getById(id, token, NAME_SERVICE);
        // if (document == null) {
        //     document = documentRepo.findById(id).orElseThrow();
        //     redisDocumentService.saveToRedis(document, token, NAME_SERVICE);
        // }
        Document document = documentRepo.findById(id).orElseThrow();
        return document;
    }

    @Override
    @Transactional
    public Document save(Document e, String token) {
        boolean isCreate = (e.getDocumentId() == null || !documentRepo.existsById(e.getDocumentId()));
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisDocumentService.getCurrentUserId(token));
        if (e.getDocumentId() == null) {
            e.setCreatedAt(CURRENT_TIME);
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

    // @Transactional
    // public Document saveFile(MultipartFile multipartFile) {
    //     String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
    //     try {
    //         if (fileName.contains("..")) {
    //             throw new Exception("File name contain invalid sequence" + fileName);
    //         }
    //         String documentId = UUID.randomUUID().toString();
    //         Document document = new Document(documentId, fileName, "/document-service/downloadFile/" + documentId, multipartFile.getContentType(), multipartFile.getBytes());
    //         return documentRepo.save(document);
    //     } catch (Exception e) {
    //         System.out.println(e);
    //     }
    //     return null;
    // }

    @Transactional
    public Document saveFile(MultipartFile file) {
        File newFile = new File(LOCATION_DIRECTORY + File.separator + file.getOriginalFilename());
        try {
            Files.copy(file.getInputStream(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            String documentId = UUID.randomUUID().toString();
            Document document = new Document(documentId, file.getOriginalFilename(), "/document-service/downloadFile/"+documentId, file.getContentType(), newFile.getPath());
            return documentRepo.save(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Document> searchByFields(Predicate<Document> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }



}
