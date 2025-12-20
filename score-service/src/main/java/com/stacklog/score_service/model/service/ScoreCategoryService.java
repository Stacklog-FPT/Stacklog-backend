package com.stacklog.score_service.model.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.score_service.model.entities.ScoreCategory;
import com.stacklog.score_service.model.repo.ScoreCategoryRepo;

import jakarta.transaction.Transactional;
import lombok.Data;

@Service
public class ScoreCategoryService implements IService<ScoreCategory> {

    private static final String NAME_SERVICE = "score-service";

    private static final String KAFKA_TOPIC_UPDATE = "score-service.scorecategory.updated";
    private static final String KAFKA_TOPIC_CREATE = "score-service.scorecategory.created";

    @Autowired
    private ScoreCategoryRepo scoreCategoryRepo;

    @Autowired
    private KafkaProducer<ScoreCategory> kafkaScoreCategoryProducer;

    private final RedisService<ScoreCategory> redisScoreCategoryService;

    public ScoreCategoryService(RedisService<ScoreCategory> redisScoreCategoryService) {
        this.redisScoreCategoryService = redisScoreCategoryService;
    }

    @Override
    public List<ScoreCategory> getAllByUserId(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllByUserId'");
    }

    public List<ScoreCategory> getAllByClassId(String classId, String token) {
        String suffix = "class:" + classId;
        List<ScoreCategory> lists = redisScoreCategoryService.getAllBySuffix(token, NAME_SERVICE, suffix);
        if (lists == null || lists.isEmpty()) {
            lists = scoreCategoryRepo.findAllByClassId(classId);
            redisScoreCategoryService.saveListToRedisWithSuffix(lists, token, NAME_SERVICE, suffix);
        }
        return lists;
    }

    public List<ScoreCategory> getAllByReused(String token) {
        List<ScoreCategory> list = redisScoreCategoryService.getAll(token, NAME_SERVICE);
        if (list == null || list.isEmpty()) {
            list = scoreCategoryRepo.findAllByIsReusable(true);
            redisScoreCategoryService.saveListToRedis(list, token, NAME_SERVICE);
        } else {
            list = list.stream().filter(sc -> sc.getIsReusable() == true).toList();
        }
        return list;
    }

    @Override
    public ScoreCategory getById(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    public ScoreCategory getByName(String name, String classId) {
        return scoreCategoryRepo.findByScoreCategoryNameAndClassId(name, classId).orElseThrow();
    }

    @Override
    public ScoreCategory save(ScoreCategory e, String token) {
        boolean isCreate = (e.getScoreCategoryId() == null || !scoreCategoryRepo.existsById(e.getScoreCategoryId()));
        e = saveToDB(e, token, isCreate);

        ScoreCategory newScoreCategory = scoreCategoryRepo.findById(e.getScoreCategoryId()).orElseThrow();

        if (isCreate) {
            kafkaScoreCategoryProducer.sendMessage(e, KAFKA_TOPIC_CREATE);
        } else {
            kafkaScoreCategoryProducer.sendMessage(e, KAFKA_TOPIC_UPDATE);
        }

        redisScoreCategoryService.saveToRedis(e, token, NAME_SERVICE);

        return newScoreCategory;
    }

    @Transactional
    private ScoreCategory saveToDB(ScoreCategory e, String token, boolean isCreate) {
        LocalDateTime now = CommonFunction.getCurrentTime();
        String currentUserId = redisScoreCategoryService.getCurrentUserId(token);

        e.setUpdateAt(now);
        e.setUpdateBy(currentUserId);
        if (isCreate) {
            e.setCreatedAt(now);
            e.setCreatedBy(currentUserId);
            e.setScoreCategoryId(UUID.randomUUID().toString());
        }
        e = scoreCategoryRepo.save(e);

        return e;
    }

    @Override
    public ScoreCategory delete(String id, String token) {
        ScoreCategory scoreCategory = scoreCategoryRepo.findById(id).orElseThrow();

        scoreCategoryRepo.deleteById(id);

        // Rebuild cache theo classId
        List<ScoreCategory> lists = scoreCategoryRepo.findAllByClassId(scoreCategory.getClassId());
        redisScoreCategoryService.saveListToRedis(lists, token, NAME_SERVICE);

        return scoreCategory;
    }

    @Transactional
    private void createDefaultScoreCategories(String classId) {
        // final, assignment, group-project, practicalexam, progresstest1, progresstest2
        List<ScoreCategory> defaultScoreCategories = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ScoreCategory sc = new ScoreCategory();
            sc.setScoreCategoryId(UUID.randomUUID().toString());
            sc.setClassId(classId);
            String name = "final";
            Double weight = 0.4;
            switch (i) {
                case 1:
                    name = "On-going Assessment 1";
                    weight = 0.2;
                    break;
                case 2:
                    name = "On-going Assessment 2";
                    weight = 0.2;
                    break;
                case 3:
                    name = "On-going Assessment 3";
                    weight = 0.2;
                    break;
                case 4:
                    name = "Final Project Presentation";
                    weight = 0.2;
                    break;
                case 5:
                    name = "assignment";
                    weight = 0.2;
                    break;
                default:
                    break;
            }
            sc.setScoreCategoryName(name);
            sc.setScoreCategoryWeight(weight);
            sc.setCreatedAt(LocalDateTime.now());
            sc.setUpdateAt(LocalDateTime.now());
            sc.setCreatedBy("");
            sc.setUpdateBy("");
            defaultScoreCategories.add(sc);
        }
        scoreCategoryRepo.saveAll(defaultScoreCategories);
    }

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = "class-service.classes.created", groupId = "score-service-group")
    public void listenClassCreated(String json) {
        System.out.println("📥 Received new class: " + json);
        ClassCreatedEvent event = null;
        try {
            event = objectMapper.readValue(json, ClassCreatedEvent.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        createDefaultScoreCategories(event.getClassesId());
    }

}

@Data
class ClassCreatedEvent {
    private String createdBy;
    private LocalDateTime createdAt;
    private String updateBy;
    private LocalDateTime updateAt;
    private String classesId;
    private String classesName;
    private String lectureId;
    private Object groups;
}
