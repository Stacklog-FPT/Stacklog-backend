package com.stacklog.chat_service.model.entities;

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
public class ChatMessage extends CoreEntity {
    
    @Id
    private String chatMessageId;

    private String chatMessageContent;
    private String chatMessageAttachment;

    private String chatMessageSendTo;

    @ManyToOne
    @JoinColumn(name = "boxChatId")
    private BoxChat boxChat;

}
