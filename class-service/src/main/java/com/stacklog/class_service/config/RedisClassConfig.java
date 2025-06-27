package com.stacklog.class_service.config;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.class_service.model.entities.Classes;
import com.stacklog.class_service.model.entities.GroupStudent;
import com.stacklog.class_service.model.entities.Groupss;
import com.stacklog.core_service.utils.jwt.JwtDecoder;
import com.stacklog.core_service.utils.redis.RedisService;

@Configuration
public class RedisClassConfig {
    

    @Bean
    public RedisService<Classes> redisClassService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder
    ) {
        return new RedisService<>(Classes.class, jwtDecoder, Classes::getClassesId);
    }

    @Bean
    public RedisService<Groupss> redisGroupService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder
    ) {
        return new RedisService<>(Groupss.class, jwtDecoder, Groupss::getGroupsId);
    }

    @Bean
    public RedisService<GroupStudent> redisGroupStudentService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder
    ) {
        return new RedisService<>(GroupStudent.class, jwtDecoder, GroupStudent::getGroupStudentId);
    }

}
