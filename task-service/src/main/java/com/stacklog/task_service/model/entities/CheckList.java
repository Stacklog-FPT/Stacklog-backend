package com.stacklog.task_service.model.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class CheckList extends CoreEntity {

    @Id
    private String checkListId;

    private String checkListName;

    @OneToMany(mappedBy = "checkItemId")
    private List<CheckItem> listItems;

    @ManyToOne
    @JoinColumn(name = "taskId")
    @JsonIgnore
    private Task task;

    public CheckList(String createdBy, String createdAt, String updateBy, String updateAt,
            String checkListName, List<CheckItem> listItems, Task task) {
        super(createdBy, createdAt, updateBy, updateAt);
        this.checkListName = checkListName;
        this.listItems = listItems;
        this.task = task;
    }

    public CheckList() {
    }

    
    

}
