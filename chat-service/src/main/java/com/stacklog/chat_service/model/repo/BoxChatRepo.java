package com.stacklog.chat_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stacklog.chat_service.model.entities.BoxChat;

@Repository
public interface BoxChatRepo extends JpaRepository<BoxChat, String>  {

    List<BoxChat> findAllByUserId(String currentUserId);
    
}
