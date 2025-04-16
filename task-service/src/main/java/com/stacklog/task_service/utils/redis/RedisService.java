package com.stacklog.task_service.utils.redis;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RedisService<E> {

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

    public List<E> getDatasFromRedis() {
        Object cached = redisTemplate.opsForValue().get(clazz.getName());
        if (cached instanceof String) {
            String jsonString = (String) cached;
            try {
                JavaType type = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, clazz);

                return objectMapper.readValue(jsonString, type);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return Collections.emptyList();
    }

    public E getDataFromRedisById(String eId) {
        Object cached = redisTemplate.opsForValue().get(key(eId));
        if (clazz.isInstance(cached)) {
            return clazz.cast(cached);
        }
        return null;
    }

    public void saveToRedis(E e, String eId, Duration ttl) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(e);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        redisTemplate.opsForValue().set(key(eId), json, ttl);
    }

    public void deleteFromRedis(String eId) {
        redisTemplate.delete(key(eId));
    }

    public void getUserCurrent() {
        
    }

}
