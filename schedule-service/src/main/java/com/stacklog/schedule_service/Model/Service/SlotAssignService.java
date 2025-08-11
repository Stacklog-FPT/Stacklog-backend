package com.stacklog.schedule_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.schedule_service.model.entities.SlotAssign;
import com.stacklog.schedule_service.model.repo.SlotAssignRepo;

@Service
public class SlotAssignService implements IService<SlotAssign> {

    private static final String NAME_SERVICE = "schedule-service";

    private static final String KAFKA_TOPIC_UPDATE = "schedule-service.slotAssign.updated";
    private static final String KAFKA_TOPIC_CREATE = "schedule-service.slotAssign.created";

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    @Autowired SlotAssignRepo slotAssignRepo;

    @Autowired
    private KafkaProducer<SlotAssign> kafkaSlotProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    RedisService<SlotAssign> redisSlotService;

    public SlotAssignService(RedisService<SlotAssign> redisSlotService) {
        this.redisSlotService = redisSlotService;
    }

    @Override
    public SlotAssign delete(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<SlotAssign> getAllByUserId(String token) {
        List<SlotAssign> slotes = redisSlotService.getAll(token, NAME_SERVICE);
        if (slotes.isEmpty()) {
            slotes = slotAssignRepo.findByUserId(redisSlotService.getCurrentUserId(token));
            redisSlotService.saveListToRedis(slotes, token, NAME_SERVICE);
        } 
        return slotes;
    }

    @Override
    public SlotAssign getById(String id, String token) {
        SlotAssign slotAssign = redisSlotService.getById(id, token, NAME_SERVICE);
        if (slotAssign == null) {
            slotAssign = slotAssignRepo.findById(id).orElseThrow();
            redisSlotService.saveToRedis(slotAssign, token, NAME_SERVICE);
        }
        return slotAssign;
    }

    @Override
    @Transactional
    public SlotAssign save(SlotAssign e, String token) {
        boolean isCreate = (e.getSlotAssignId() == null || !slotAssignRepo.existsById(e.getSlotAssignId()));
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisSlotService.getCurrentUserId(token));
        if (e.getSlotAssignId() == null || e.getSlotAssignId().isBlank()) {
            e.setCreatedAt(CURRENT_TIME);
            e.setCreatedBy(redisSlotService.getCurrentUserId(token));
            e.setSlotAssignId(UUID.randomUUID().toString());
        }
        if (isCreate) {
            kafkaSlotProducer.sendMessage(e, KAFKA_TOPIC_CREATE);
        } else {
            kafkaSlotProducer.sendMessage(e, KAFKA_TOPIC_UPDATE);
        }

        redisSlotService.saveToRedis(e, token, NAME_SERVICE);

        messagingTemplate.convertAndSend("/topic/schedule-service", e);

        slotAssignRepo.save(e);

        return e;
    }

    public List<SlotAssign> getAllBySlotId(String token, String slotId) {
        List<SlotAssign> slotAssigns = redisSlotService.getAll(token, NAME_SERVICE);
        if (slotAssigns.isEmpty()) {
            slotAssigns = slotAssignRepo.findBySlotSlotId(slotId);
            redisSlotService.saveListToRedis(slotAssigns, token, NAME_SERVICE);
        }
        return slotAssigns;
    }
    
}
