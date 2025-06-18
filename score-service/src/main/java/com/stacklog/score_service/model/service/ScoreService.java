package com.stacklog.score_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.score_service.model.entities.Score;
import com.stacklog.score_service.model.repo.ScoreRepo;

@Service
public class ScoreService implements IService<Score> {

    private static final String NAME_SERVICE = "score-service";

    private static final String KAFKA_TOPIC_UPDATE = "score-service.score.updated";
    private static final String KAFKA_TOPIC_CREATE = "score-service.score.created";

    private LocalDateTime CURRENT_TIME = CommonFunction.getCurrentTime();

    @Autowired
    private ScoreRepo scoreRepo;

    @Autowired
    KafkaProducer<Score> kafkaProducerScore;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    RedisService<Score> redisScoreService;

    public ScoreService(RedisService<Score> redisScoreService) {
        this.redisScoreService = redisScoreService;
    }

    @Override
    public Score delete(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<Score> getAllByUserId(String token) {
        List<Score> scores = redisScoreService.getAll(token, NAME_SERVICE);
        if (scores.isEmpty()) {
            scores = scoreRepo.findByUserId(redisScoreService.getCurrentUserId(token));
            redisScoreService.saveListToRedis(scores, token, NAME_SERVICE);
        }
        return scores;
    }

    @Override
    public Score getById(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public Score save(Score e, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public List<Score> searchByFields(Predicate<Score> p, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByFields'");
    }
    
}
