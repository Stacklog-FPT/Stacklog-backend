package com.stacklog.task_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.entities.TaskAssign;
import com.stacklog.task_service.utils.jwt.JwtDecoder;
import com.stacklog.task_service.utils.redis.RedisService;

@Configuration
public class RedisConfig {

    @Bean
    public RedisService<Task> redisTaskService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder
    ) {
        return new RedisService<>(Task.class, jwtDecoder);
    }

    @Bean
    public RedisService<TaskAssign> redisTaskAssignService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder
    ) {
        return new RedisService<>(TaskAssign.class, jwtDecoder);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

}
