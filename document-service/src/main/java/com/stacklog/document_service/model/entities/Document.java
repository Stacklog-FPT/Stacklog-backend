package com.stacklog.document_service.model.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stacklog.core_service.model.entities.CoreEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Document extends CoreEntity {
    
    @Id
    private String documentId;

    private String documentTitle;
    private String documentDownloadUri;
    private String documentContentType;

    private String documentPath;

    @OneToMany(mappedBy = "document")
    @JsonIgnore
    private List<DocumentLocation> documentLocations;


}
