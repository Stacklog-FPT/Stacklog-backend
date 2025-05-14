package com.stacklog.chat_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stacklog.chat_service.model.entities.BoxChat;
import com.stacklog.chat_service.model.repo.BoxChatRepo;
import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;

@Service
public class BoxChatService implements IService<BoxChat> {

    private static final String NAME_SERVICE = "chat-service";

    private static final String KAFKA_TOPIC_UPDATE = "chat-service.boxchat.updated";
    private static final String KAFKA_TOPIC_CREATE = "chat-service.boxchat.created";

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    @Autowired
    BoxChatRepo boxChatRepo;

    @Autowired
    KafkaProducer<BoxChat> kafkaBoxChatProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    RedisService<BoxChat> redisBoxChatService;

    public BoxChatService(RedisService<BoxChat> redisBoxChatService) {
        this.redisBoxChatService = redisBoxChatService;
    }

    @Override
    public BoxChat delete(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<BoxChat> getAllByUserId(String token) {
        List<BoxChat> boxChats = redisBoxChatService.getAll(token, NAME_SERVICE);
        if (boxChats.isEmpty() || boxChats == null) {
            boxChats = boxChatRepo.findAllByUserId(redisBoxChatService.getCurrentUserId(token));
            redisBoxChatService.saveListToRedis(boxChats, token, NAME_SERVICE);
        }
        return boxChats;
    }

    @Override
    public BoxChat getById(String id, String token) {
        BoxChat boxChat = redisBoxChatService.getById(id, token, NAME_SERVICE);
        if (boxChat == null) {
            boxChat = boxChatRepo.findById(id).orElseThrow();
            redisBoxChatService.saveToRedis(boxChat, token, NAME_SERVICE);
        }
        return boxChat;
    }

    @Override
    @Transactional
    public BoxChat save(BoxChat e, String token) {
        boolean isCreate = (e.getBoxChatId() == null || !boxChatRepo.existsById(e.getBoxChatId()));
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisBoxChatService.getCurrentUserId(token));
        if (e.getBoxChatId() == null) {
            e.setCreatedAt(CURRENT_TIME);
            e.setCreatedBy(redisBoxChatService.getCurrentUserId(token));
            e.setBoxChatId(UUID.randomUUID().toString());
        }
        if (isCreate) {
            kafkaBoxChatProducer.sendMessage(e, KAFKA_TOPIC_CREATE);
        } else {
            kafkaBoxChatProducer.sendMessage(e, KAFKA_TOPIC_UPDATE);
        }

        redisBoxChatService.saveToRedis(e, token, NAME_SERVICE);

        messagingTemplate.convertAndSend("/topic/chat-service", e);

        return e;
    }

    @Override
    public List<BoxChat> searchByFields(Predicate<BoxChat> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }
    
}
