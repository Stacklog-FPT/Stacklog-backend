package com.stacklog.chat_service.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stacklog.core_service.model.entities.CoreEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BoxChatUser extends CoreEntity {
    
    @Id
    private String boxChatUserId;

    private String userId;
    private boolean isMute;
    private boolean isAdmin;

    @ManyToOne
    @JoinColumn(name = "boxChatId")
    @JsonIgnore
    private BoxChat boxChat;

}
