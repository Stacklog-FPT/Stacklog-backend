package com.stacklog.task_service.utils.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.task_service.utils.jwt.JwtDecoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisService<E> {

    private final Duration ttl = Duration.ofMinutes(5);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final Class<E> clazz;

    public RedisService(Class<E> clazz) {
        this.clazz = clazz;
    }

    private String key(String id) {
        return clazz.getName() + "::" + id;
    }

    private String key(String id, String status) {
        return clazz.getName() + "::" + id + "::" + status;
    }

    public List<E> getDatas() {
        Set<String> keys = redisTemplate.keys(clazz.getName() + "*::*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        List<E> results = new ArrayList<>();
        for (String key : keys) {
            String json = redisTemplate.opsForValue().get(key);
            try {
                E obj = objectMapper.readValue(json, clazz);
                results.add(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public E getDataById(String eId) {
        String json = redisTemplate.opsForValue().get(key(eId));
        if (json != null) {
            try {
                return objectMapper.readValue(json, clazz);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<E> getAllByStatus(String status) {
        Set<String> keys = redisTemplate.keys(clazz.getName() + "*::" + status);
        if (keys == null || keys.isEmpty())
            return Collections.emptyList();

        List<E> results = new ArrayList<>();
        for (String key : keys) {
            String json = redisTemplate.opsForValue().get(key);
            try {
                results.add(objectMapper.readValue(json, clazz));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public void saveListToRedis(List<E> list) {
        list.forEach(e -> {
            String id = extractId(e);
            if (id != null) {
                saveToRedis(e, id, "SUCCESS_WRITE");
            }
        });
    }

    private String extractId(E e) {
        try {
            String methodGetId = "get" + clazz.getSimpleName() + "Id";
            return e.getClass().getMethod(methodGetId).invoke(e).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public E saveToRedis(E e, String eId, String status) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(e);
            redisTemplate.opsForValue().set(key(eId, status), json, ttl);
        } catch (JsonProcessingException e1) {
            log.error("❌ Failed to serialize object of type {}: {}", e.getClass().getName(), e1.getMessage());
        }
        return e;

    }

    public E deleteFromRedis(String eId) {
        String json = redisTemplate.opsForValue().get(key(eId));
        if (json != null) {
            try {
                E value = objectMapper.readValue(json, clazz);
                redisTemplate.delete(key(eId));
                return value;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getCurrentUserId() {
        return new JwtDecoder().getIdFromToken(redisTemplate.opsForValue().get("currentuser"));
    }

}
