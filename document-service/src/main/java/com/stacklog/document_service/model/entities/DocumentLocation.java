package com.stacklog.document_service.model.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DocumentLocation {
    
    @Id
    private String documentLocationId;

    private String groupId;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

}
