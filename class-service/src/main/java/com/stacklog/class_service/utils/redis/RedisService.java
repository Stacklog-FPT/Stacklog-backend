package com.stacklog.class_service.utils.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.class_service.utils.jwt.JwtDecoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisService<E> {

    private final Duration ttl = Duration.ofMinutes(5);

    private final JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final Class<E> clazz;

    public RedisService(Class<E> clazz, JwtDecoder jwtDecoder) {
        this.clazz = clazz;
        this.jwtDecoder = jwtDecoder;
    }

    // ===== Create a key for E
    public String getKey(String currentUserId, String subtype) {
        return "class-service:" + clazz.getSimpleName() + ":" + currentUserId + ":" + subtype;
    }

    public List<E> getAll(String token) {
        String currentUserId = getCurrentUserId(token);
        Set<String> keys = redisTemplate.keys();
    }

    // ===== Get current userId trong Redis
    public String getCurrentUserId(String token) {
        Set<String> keys = redisTemplate.keys("auth:session:*");
        if (keys.isEmpty()) {
            throw new RuntimeException("❌ Token không tồn tại trong Redis");
        }
        String currentId = jwtDecoder.getIdFromToken(token);
        if (keys.stream()
                .anyMatch(key -> jwtDecoder.getIdFromToken(redisTemplate.opsForValue().get(key)).equals(currentId))) {
            return currentId;
        }
        return null;
    }
}
