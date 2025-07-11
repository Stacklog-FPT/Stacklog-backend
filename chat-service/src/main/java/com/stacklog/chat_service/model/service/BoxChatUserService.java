package com.stacklog.chat_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stacklog.chat_service.model.entities.BoxChatUser;
import com.stacklog.chat_service.model.repo.BoxChatUserRepo;
import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;

@Service
public class BoxChatUserService implements IService<BoxChatUser> {

    private static final String NAME_SERVICE = "chat-service";

    private static final String KAFKA_TOPIC_UPDATE = "chat-service.boxchatuser.updated";
    private static final String KAFKA_TOPIC_CREATE = "chat-service.boxchatuser.created";

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    @Autowired
    BoxChatUserRepo boxChatUserRepo;

    @Autowired
    KafkaProducer<BoxChatUser> kafkaBoxChatUserProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    RedisService<BoxChatUser> redisBoxChatUserService;

    public BoxChatUserService(RedisService<BoxChatUser> redisBoxChatUserService) {
        this.redisBoxChatUserService = redisBoxChatUserService;
    }

    @Override
    public BoxChatUser delete(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<BoxChatUser> getAllByUserId(String token) {
        List<BoxChatUser> boxChatUsers = redisBoxChatUserService.getAll(token, NAME_SERVICE);
        if (boxChatUsers.isEmpty() || boxChatUsers == null) {
            boxChatUsers = boxChatUserRepo.findAllByUserId(redisBoxChatUserService.getCurrentUserId(token));
            redisBoxChatUserService.saveToRedis(null, token, NAME_SERVICE);
        }
        return boxChatUsers;
    }

    public List<BoxChatUser> getAllByBoxChatId(String boxChatId, String token) {
        List<BoxChatUser> boxChatUsers = redisBoxChatUserService.getAll(token, NAME_SERVICE).stream()
                .filter(bcu -> bcu.getBoxChat().getBoxChatId().equals(boxChatId)).toList();
        if (boxChatUsers.isEmpty() || boxChatUsers == null) {
            boxChatUsers = boxChatUserRepo.findAllByBoxChatId(boxChatId);
            redisBoxChatUserService.saveListToRedis(boxChatUsers, token, NAME_SERVICE);
        }
        return boxChatUsers;
    }

    @Override
    public BoxChatUser getById(String id, String token) {
        BoxChatUser boxChatUser = redisBoxChatUserService.getById(id, token, NAME_SERVICE);
        if (boxChatUser == null) {
            boxChatUser = boxChatUserRepo.findById(id).orElseThrow();
            redisBoxChatUserService.saveToRedis(boxChatUser, token, NAME_SERVICE);
        }
        return boxChatUser;
    }

    @Override
    @Transactional
    public BoxChatUser save(BoxChatUser e, String token) {
        boolean isCreate = (e.getBoxChatUserId() == null || !boxChatUserRepo.existsById(e.getBoxChatUserId()));
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisBoxChatUserService.getCurrentUserId(token));
        if (e.getBoxChatUserId() == null) {
            e.setCreatedAt(CURRENT_TIME);
            e.setCreatedBy(redisBoxChatUserService.getCurrentUserId(token));
            e.setBoxChatUserId(UUID.randomUUID().toString());
        }
        if (isCreate) {
            kafkaBoxChatUserProducer.sendMessage(e, KAFKA_TOPIC_CREATE);
        } else {
            kafkaBoxChatUserProducer.sendMessage(e, KAFKA_TOPIC_UPDATE);
        }

        redisBoxChatUserService.saveToRedis(e, token, NAME_SERVICE);

        messagingTemplate.convertAndSend("/topic/chat-service", e);

        e = boxChatUserRepo.save(e);

        return e;
    }

    @Override
    public List<BoxChatUser> searchByFields(Predicate<BoxChatUser> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }

}
