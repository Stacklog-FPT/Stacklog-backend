package com.stacklog.document_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stacklog.document_service.model.entities.Document;
import com.stacklog.document_service.model.service.DocumentService;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/document-service")
public class DocumentRestController {
    
    @Autowired DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadFile(@RequestParam(name = "file") MultipartFile multipartFile) {
        String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        long size = multipartFile.getSize();

        documentService.saveFile(filename, multipartFile);

        Document document = new Document();
        document.setDocumentTitle(filename);
        document.setDownloadUri("/downloadFile");
        document.setSize(size);
        
        return ResponseEntity.ok().body(document);
    }
    

}
