package com.stacklog.class_service.model.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Classes extends CoreEntity {

    @Id
    private String classesId;

    private String classesName;
    private String lectureId;

    @OneToMany(mappedBy = "classes")
    private List<Groupss> groups;
}
