package com.stacklog.chat_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stacklog.chat_service.model.entities.BoxChatUser;

@Repository
public interface BoxChatUserRepo extends JpaRepository<BoxChatUser, String> {

    List<BoxChatUser> findAllByUserId(String currentUserId);

    List<BoxChatUser> findAllByBoxChatId(String boxChatId);
    
}
