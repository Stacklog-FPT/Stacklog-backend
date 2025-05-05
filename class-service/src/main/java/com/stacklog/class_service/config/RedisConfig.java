package com.stacklog.class_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.class_service.model.entities.Classes;
import com.stacklog.class_service.model.entities.GroupStudent;
import com.stacklog.class_service.model.entities.Groupss;
import com.stacklog.class_service.utils.jwt.JwtDecoder;
import com.stacklog.class_service.utils.redis.RedisService;


@Configuration
public class RedisConfig {

    @Bean
    public RedisService<Classes> redisClassService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder
    ) {
        return new RedisService<>(Classes.class, jwtDecoder);
    }

    @Bean
    public RedisService<Groupss> redisGroupssService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder
    ) {
        return new RedisService<>(Groupss.class, jwtDecoder);
    }

    @Bean
    public RedisService<GroupStudent> redisGroupsStudentService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder
    ) {
        return new RedisService<>(GroupStudent.class, jwtDecoder);
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
