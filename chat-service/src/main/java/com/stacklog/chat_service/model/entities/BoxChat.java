package com.stacklog.chat_service.model.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stacklog.core_service.model.entities.CoreEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BoxChat extends CoreEntity {

    @Id
    private String boxChatId;

    private String nameBox;
    private String avaBox;

    @OneToMany(mappedBy = "boxChat")
    @JsonIgnore
    private List<ChatMessage> chatMessages;

    @OneToMany(mappedBy = "boxChat")
    @JsonIgnore
    private List<BoxChatUser> boxChatUsers;

}
