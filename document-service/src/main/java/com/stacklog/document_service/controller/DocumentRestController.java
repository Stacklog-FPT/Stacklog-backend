package com.stacklog.document_service.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.document_service.model.entities.Document;
import com.stacklog.document_service.model.service.DocumentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/document-service")
public class DocumentRestController {

    @Autowired
    DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Document> uploadFile(@RequestParam(name = "file") MultipartFile multipartFile,
            @RequestParam("document") String eStr, 
            @RequestHeader("Authorization") String token) {
        
        Document document = convertStringToDocument(eStr);
        document = documentService.saveFile(multipartFile, document, token);
        return ResponseEntity.ok().body(document);
    }

    @GetMapping("/downloadFile/{documentId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "documentId") String documentId,
            @RequestHeader("Authorization") String token) {
        Document document = documentService.getById(documentId, token);
        File fileDownload = new File(document.getDocumentPath());
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getDocumentContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "document; filename=\"" + document.getDocumentTitle() + "\"")
                    .body(new InputStreamResource(Files.newInputStream(fileDownload.toPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }


    private Document convertStringToDocument(String eString) {
        String json = "{\"documentId\":\"\",\"documentTitle\":\"\",\"documentContentType\":\"\",\"documentDownloadUri\":\"\"}";

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Document doc = objectMapper.readValue(json, Document.class);
            return doc;
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
