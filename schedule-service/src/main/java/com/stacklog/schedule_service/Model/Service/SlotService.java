package com.stacklog.schedule_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.schedule_service.model.entities.Slot;
import com.stacklog.schedule_service.model.repo.SlotRepo;

@Service
public class SlotService implements IService<Slot> {

    private static final String NAME_SERVICE = "schedule-service";

    private static final String KAFKA_TOPIC_UPDATE = "schedule-service.slot.updated";
    private static final String KAFKA_TOPIC_CREATE = "schedule-service.slot.created";

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    @Autowired SlotRepo slotRepo;

    @Autowired
    private KafkaProducer<Slot> kafkaSlotProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    RedisService<Slot> redisSlotService;

    public SlotService(RedisService<Slot> redisSlotService) {
        this.redisSlotService = redisSlotService;
    }

    @Override
    public Slot delete(String id, String token) {
        Slot slot = getById(id, token);
        slotRepo.delete(slot);
        return slot;
    }

    @Override
    public List<Slot> getAllByUserId(String token) {
        List<Slot> slotes = redisSlotService.getAll(token, NAME_SERVICE);
        if (slotes.isEmpty()) {
            slotes = slotRepo.findByUserId(redisSlotService.getCurrentUserId(token));
            redisSlotService.saveListToRedis(slotes, token, NAME_SERVICE);
        } 
        return slotes;
    }

    @Override
    public Slot getById(String id, String token) {
        Slot slot = redisSlotService.getById(id, token, NAME_SERVICE);
        if (slot == null) {
            slot = slotRepo.findById(id).orElseThrow();
            redisSlotService.saveToRedis(slot, token, NAME_SERVICE);
        }
        return slot;
    }

    @Override
    @Transactional
    public Slot save(Slot e, String token) {
        boolean isCreate = (e.getSlotId() == null || !slotRepo.existsById(e.getSlotId()));
        e.setUpdateAt(CURRENT_TIME);
        e.setUpdateBy(redisSlotService.getCurrentUserId(token));
        if (e.getSlotId() == null || e.getSlotId().isBlank()) {
            e.setCreatedAt(CURRENT_TIME);
            e.setCreatedBy(redisSlotService.getCurrentUserId(token));
            e.setSlotId(UUID.randomUUID().toString());
        }
        if (isCreate) {
            kafkaSlotProducer.sendMessage(e, KAFKA_TOPIC_CREATE);
        } else {
            kafkaSlotProducer.sendMessage(e, KAFKA_TOPIC_UPDATE);
        }

        redisSlotService.saveToRedis(e, token, NAME_SERVICE);

        messagingTemplate.convertAndSend("/topic/task-service", e);

        slotRepo.save(e);

        return e;
    }

    @Override
    public List<Slot> searchByFields(Predicate<Slot> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }

    public List<Slot> getAllByGroupId(String token, String groupId) {
        List<Slot> slots = redisSlotService.getAll(token, NAME_SERVICE);
        if (slots.isEmpty()) {
            slots = slotRepo.findByGroupId(groupId);
            redisSlotService.saveListToRedis(slots, token, NAME_SERVICE);
        }
        return slots;
    }
    
}
