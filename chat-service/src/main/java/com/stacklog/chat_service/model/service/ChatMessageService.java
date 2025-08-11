package com.stacklog.chat_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stacklog.chat_service.model.entities.ChatMessage;
import com.stacklog.chat_service.model.repo.ChatMessageRepo;
import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;

@Service
public class ChatMessageService implements IService<ChatMessage> {

    private static final String NAME_SERVICE = "chat-service";

    private static final String KAFKA_TOPIC_UPDATE = "chat-service.chatmessage.updated";
    private static final String KAFKA_TOPIC_CREATE = "chat-service.chatmessage.created";

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    @Autowired
    ChatMessageRepo chatMessageRepo;

    @Autowired
    KafkaProducer<ChatMessage> kafkaChatMessageProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    RedisService<ChatMessage> redisChatMessageService;

    public ChatMessageService(RedisService<ChatMessage> redisChatMessageService) {
        this.redisChatMessageService = redisChatMessageService;
    }

    @Override
    public ChatMessage delete(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<ChatMessage> getAllByUserId(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllByUserId'");
    }

    public List<ChatMessage> getAllByBoxChatId(String token, String boxChatId) {
        List<ChatMessage> chatMessages = redisChatMessageService.getAll(token, NAME_SERVICE).stream().filter(cm -> cm.getBoxChat().getBoxChatId().equals(boxChatId)).toList();
        if (chatMessages.isEmpty() || chatMessages == null) {
            chatMessages = chatMessageRepo.findAllByBoxChatId(boxChatId);
            redisChatMessageService.saveListToRedis(chatMessages, token, boxChatId);
        }
        return chatMessages;
    }

    @Override
    public ChatMessage getById(String id, String token) {
        ChatMessage chatMessage = redisChatMessageService.getById(id, token, NAME_SERVICE);
        if (chatMessage == null) {
            chatMessage = chatMessageRepo.findById(id).orElseThrow();
            redisChatMessageService.saveToRedis(chatMessage, token, NAME_SERVICE);
        }
        return chatMessage;
    }

    @Override
    @Transactional
    public ChatMessage save(ChatMessage e, String token) {
        boolean isCreate = (e.getChatMessageId() == null || !chatMessageRepo.existsById(e.getChatMessageId()));
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisChatMessageService.getCurrentUserId(token));
        if (e.getChatMessageId() == null) {
            e.setCreatedAt(CURRENT_TIME);
            e.setCreatedBy(redisChatMessageService.getCurrentUserId(token));
            e.setChatMessageId(UUID.randomUUID().toString());
        }
        if (isCreate) {
            kafkaChatMessageProducer.sendMessage(e, KAFKA_TOPIC_CREATE);
        } else {
            kafkaChatMessageProducer.sendMessage(e, KAFKA_TOPIC_UPDATE);
        }

        redisChatMessageService.saveToRedis(e, token, NAME_SERVICE);

        messagingTemplate.convertAndSend("/topic/chat-service", e);

        e = chatMessageRepo.save(e);

        return e;
    }
    
}
