package com.stacklog.core_service.utils.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.core_service.utils.jwt.JwtDecoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisService<E> {

    private final Duration ttl = Duration.ofMinutes(1440);

    private final JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private Function<E, String> idExtractor;

    private final Class<E> clazz;

    public RedisService(Class<E> clazz, JwtDecoder jwtDecoder, Function<E, String> idExtractor) {
        this.clazz = clazz;
        this.jwtDecoder = jwtDecoder;
        this.idExtractor = idExtractor;
    }

    // ===== Create a key for E
    public String getKey(String currentUserId, String subtype, String nameService, String eId) {
        return nameService + ":" + clazz.getSimpleName() + ":" + currentUserId + ":" + subtype + ":" + eId;
    }

    // ===== method tạo indexKey đúng chuẩn =====
    private String getIndexKey(String currentUserId, String nameService) {
        return String.format("%s:%s:index:%s", nameService, clazz.getSimpleName(), currentUserId);
    }

    // Tạo indexKey tùy biến thêm suffix như :group:<groupId>,
    // :project:<projectId>...
    public String getCustomIndexKey(String currentUserId, String nameService, String suffix) {
        return String.format("%s:%s:index:%s:%s", nameService, clazz.getSimpleName(), currentUserId, suffix);
    }

    // get and save to redis
    public List<E> getAll(String token, String nameService) {
        String currentUserId = getCurrentUserId(token);
        String indexKey = String.format("%s:%s:%s", nameService, clazz.getSimpleName(), currentUserId);
        Set<String> keys = redisTemplate.opsForSet().members(indexKey);
        if (keys == null || keys.isEmpty())
            return Collections.emptyList();
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

    public E getById(String eId, String token, String nameService) {
        String currentUserId = getCurrentUserId(token);
        String key = getKey(currentUserId, "web", nameService, eId);
        String json = redisTemplate.opsForValue().get(key);
        try {
            return json != null ? objectMapper.readValue(json, clazz) : null;
        } catch (Exception e) {
            log.error("❌ Deserialize failed: {}", e.getMessage());
            return null;
        }
    }

    public void saveListToRedis(List<E> list, String token, String nameService) {
        deleteAllByUserId(token, nameService);
        list.forEach(e -> {
            String id = idExtractor.apply(e);
            if (id != null) {
                saveToRedis(e, token, nameService);
            }
        });
    }

    public E saveToRedis(E e, String token, String nameService) {
        String currentUserId = getCurrentUserId(token);
        try {
            String json = objectMapper.writeValueAsString(e);
            String indexKey = getIndexKey(currentUserId, nameService);
            redisTemplate.opsForValue().set(getKey(currentUserId, "web", nameService, idExtractor.apply(e)), json, ttl);
            redisTemplate.opsForSet().add(indexKey, getKey(currentUserId, "web", nameService, idExtractor.apply(e)));
        } catch (JsonProcessingException e1) {
            log.error("❌ Failed to serialize object of type {}: {}", e.getClass().getName(), e1.getMessage());
        }
        return e;
    }

    public void deleteAllByUserId(String token, String nameService) {
        String currentUserId = getCurrentUserId(token);
        String indexKey = getIndexKey(currentUserId, nameService);
        Set<String> keys = redisTemplate.opsForSet().members(indexKey);
        if (keys != null && !keys.isEmpty())
            redisTemplate.delete(keys);
        redisTemplate.delete(indexKey);
    }

    public String getCurrentUserId(String token) {
        String userId = jwtDecoder.getIdFromToken(token);
        String device = "web";
        String key = "auth:session:" + userId + ":" + device;
        String storedToken = redisTemplate.opsForValue().get(key);
        token = token.split(" ")[1];
        if (!token.equals(storedToken))
            throw new RuntimeException("Token invalid or expired");
        return userId;
    }
}
