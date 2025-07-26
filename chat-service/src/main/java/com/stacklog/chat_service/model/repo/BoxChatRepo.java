package com.stacklog.chat_service.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stacklog.chat_service.model.entities.BoxChat;

@Repository
public interface BoxChatRepo extends JpaRepository<BoxChat, String> {

    @Query("""
                SELECT bc
                FROM BoxChat bc
                JOIN bc.boxChatUsers bcu
                WHERE bcu.userId = :userId
            """)
    List<BoxChat> findAllByUserId(@Param("userId") String currentUserId);

}
