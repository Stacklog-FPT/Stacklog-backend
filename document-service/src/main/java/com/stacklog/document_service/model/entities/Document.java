package com.stacklog.document_service.model.entities;

import com.stacklog.core_service.model.entities.CoreEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Document extends CoreEntity {
    
    @Id
    private String documentId;

    private String documentTitle;
    private String documentPath;
    private String downloadUri;
    private long size;

}
