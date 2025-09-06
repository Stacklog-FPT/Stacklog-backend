package com.stacklog.task_service.model.service;

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
import com.stacklog.task_service.model.entities.CheckList;
import com.stacklog.task_service.model.repo.CheckListRepo;

@Service
public class CheckListService implements IService<CheckList> {

    

    private static final String NAME_SERVICE = "task-service";

    private static final String KAFKA_TOPIC_UPDATE = "task-service.checklist.updated";
    private static final String KAFKA_TOPIC_CREATE = "task-service.checklist.created";

    @Autowired
    CheckListRepo checkListRepo;
    @Autowired private CheckItemService checkItemService;

    @Autowired
    KafkaProducer<CheckList> checkListProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    RedisService<CheckList> redisCheckListService;

    public CheckListService(RedisService<CheckList> redisCheckListService) {
        this.redisCheckListService = redisCheckListService;
    }

    @Override
    public CheckList delete(String id, String token) {
        CheckList checkList = checkListRepo.findById(id).orElseThrow();
        checkListRepo.deleteById(id);
        return checkList;
    }

    @Override
    public List<CheckList> getAllByUserId(String token) {
        List<CheckList> checkLists = redisCheckListService.getAll(token, NAME_SERVICE);
        if (checkLists.isEmpty() || checkLists == null) {
            checkLists = checkListRepo.findAllByUserId(redisCheckListService.getCurrentUserId(token));
            redisCheckListService.saveListToRedis(checkLists, token, NAME_SERVICE);
        }
        return checkLists;
    }

    public List<CheckList> getAllByTaskId(String taskId, String token) {
        List<CheckList> checkLists = getAllByUserId(token);
        return checkLists.stream().filter(cl -> cl.getTask().getTaskId().equals(taskId)).toList();
    }

    @Override
    @Transactional
    public CheckList getById(String id, String token) {
        CheckList checkList = redisCheckListService.getById(id, token, NAME_SERVICE);
        if (checkList == null) {
            checkList = checkListRepo.findById(id).orElseThrow();
            redisCheckListService.saveToRedis(checkList, token, NAME_SERVICE);
        }
        return checkList;
    }

    @Override
    public CheckList save(CheckList e, String token) {
        boolean isCreate = (e.getCheckListId() == null || !checkListRepo.existsById(e.getCheckListId()));
        e.setUpdateAt(CommonFunction.getCurrentTime());
        e.setUpdateBy(redisCheckListService.getCurrentUserId(token));
        if (e.getCheckListId() == null) {
            e.setCreatedAt(CommonFunction.getCurrentTime());
            e.setCreatedBy(redisCheckListService.getCurrentUserId(token));
            e.setCheckListId(UUID.randomUUID().toString());
        }

        e.setCheckListId(checkListRepo.save(e).getCheckListId());

        if (e.getListItems() != null && e.getListItems().isEmpty()) {
            e.getListItems().stream().forEach(item -> checkItemService.save(item, token));
        }

        CheckList newCheckList = checkListRepo.findById(e.getCheckListId()).orElseThrow();

        redisCheckListService.saveToRedis(newCheckList, token, NAME_SERVICE);

        checkListProducer.sendMessage(newCheckList, isCreate ? KAFKA_TOPIC_CREATE : KAFKA_TOPIC_UPDATE);

        messagingTemplate.convertAndSend("/topic/task-service", e);

        return e;
    }

}
