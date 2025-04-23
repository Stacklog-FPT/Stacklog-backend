package com.stacklog.task_service.utils.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.task_service.utils.jwt.JwtDecoder;

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

    // ===== 🔑 Generate key theo userId
    private String key(String userId, String id) {
        return String.format("user:%s:%s::%s", userId, clazz.getSimpleName(), id);
    }

    private String key(String userId, String id, String status) {
        return String.format("user:%s:%s::%s::%s", userId, clazz.getSimpleName(), id, status);
    }

    // ===== ✅ Get all data của user
    public List<E> getDatas() {
        String userId = getCurrentUserId();
        Set<String> keys = redisTemplate.keys("user:" + userId + ":" + clazz.getSimpleName() + "*::*");
        if (keys == null || keys.isEmpty()) return Collections.emptyList();

        List<E> results = new ArrayList<>();
        for (String key : keys) {
            String json = redisTemplate.opsForValue().get(key);
            try {
                E obj = objectMapper.readValue(json, clazz);
                results.add(obj);
            } catch (Exception e) {
                log.error("❌ Failed to deserialize key {}: {}", key, e.getMessage());
            }
        }
        return results;
    }

    // ===== ✅ Get theo ID
    public E getDataById(String eId) {
        String userId = getCurrentUserId();
        String json = redisTemplate.opsForValue().get(key(userId, eId));
        if (json != null) {
            try {
                return objectMapper.readValue(json, clazz);
            } catch (JsonProcessingException e) {
                log.error("❌ Deserialize error: {}", e.getMessage());
            }
        }
        return null;
    }

    // ===== ✅ Get theo status
    public List<E> getAllByStatus(String status) {
        String userId = getCurrentUserId();
        Set<String> keys = redisTemplate.keys("user:" + userId + ":" + clazz.getSimpleName() + "*::*::" + status);
        if (keys == null || keys.isEmpty()) return Collections.emptyList();

        List<E> results = new ArrayList<>();
        for (String key : keys) {
            String json = redisTemplate.opsForValue().get(key);
            try {
                results.add(objectMapper.readValue(json, clazz));
            } catch (Exception e) {
                log.error("❌ Deserialize error: {}", e.getMessage());
            }
        }
        return results;
    }

    // ===== ✅ Save 1 list
    public void saveListToRedis(List<E> list) {
        list.forEach(e -> {
            String id = extractId(e);
            if (id != null) {
                saveToRedis(e, id, "SUCCESS_WRITE");
            }
        });
    }

    // ===== 🔍 Extract ID từ entity
    private String extractId(E e) {
        try {
            String methodGetId = "get" + clazz.getSimpleName() + "Id";
            return e.getClass().getMethod(methodGetId).invoke(e).toString();
        } catch (Exception ex) {
            log.error("❌ Failed to extract ID: {}", ex.getMessage());
            return null;
        }
    }

    // ===== ✅ Save 1 entity
    public E saveToRedis(E e, String eId, String status) {
        String userId = getCurrentUserId();
        deleteFromRedis(eId);
        try {
            String json = objectMapper.writeValueAsString(e);
            redisTemplate.opsForValue().set(key(userId, eId, status), json, ttl);
        } catch (JsonProcessingException e1) {
            log.error("❌ Failed to serialize object of type {}: {}", e.getClass().getName(), e1.getMessage());
        }
        return e;
    }

    // ===== ✅ Xóa khỏi Redis
    public E deleteFromRedis(String eId) {
        String userId = getCurrentUserId();
        String json = redisTemplate.opsForValue().get(key(userId, eId));
        if (json != null) {
            try {
                E value = objectMapper.readValue(json, clazz);
                redisTemplate.delete(key(userId, eId));
                return value;
            } catch (JsonProcessingException e) {
                log.error("❌ Deserialize error: {}", e.getMessage());
            }
        }
        return null;
    }

    // ===== 🔐 Get current userId từ token trong Redis
    public String getCurrentUserId() {
        String token = redisTemplate.opsForValue().get("currentuser");
        if (token == null) {
            throw new RuntimeException("❌ Token không tồn tại trong Redis");
        }
        return jwtDecoder.getIdFromToken(token);
    }
}
