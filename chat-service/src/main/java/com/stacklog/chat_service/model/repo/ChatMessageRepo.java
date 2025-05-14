package com.stacklog.chat_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stacklog.chat_service.model.entities.ChatMessage;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage, String> {

    @Query(value = "SELECT cm FROM ChatMessage cm WHERE cm.boxChat.boxChatId = :boxChatId")
    List<ChatMessage> findAllByBoxChatId(String boxChatId);
    
}
