package com.stacklog.score_service.config;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.core_service.utils.jwt.JwtDecoder;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.score_service.model.entities.Score;

@Configuration
public class RedisScoreConfig {

    @Bean
    public Function<Score, String> classIdExtractor() {
        return Score::getScoreId;
    }

    @Bean
    public RedisService<Score> redisScoreService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder) {
        return new RedisService<>(Score.class, jwtDecoder);
    }
    
}
