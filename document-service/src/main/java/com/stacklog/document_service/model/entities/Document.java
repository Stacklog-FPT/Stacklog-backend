package com.stacklog.document_service.model.entities;

import com.stacklog.core_service.model.entities.CoreEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Document extends CoreEntity {
    
    @Id
    private String documentId;

    private String documentTitle;
    private String documentDownloadUri;
    private String documentContentType;

    // @Lob
    // @Column(columnDefinition = "LONGBLOB")
    // private byte[] documentData;
    private String documentPath;

}
