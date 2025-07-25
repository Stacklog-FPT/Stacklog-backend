package com.stacklog.schedule_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.core_service.utils.jwt.JwtDecoder;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.schedule_service.Model.Entities.Slot;

@Configuration
public class RedisSlotConfig {
    

    @Bean
    public RedisService<Slot> redisSlotService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder
    ) {
        return new RedisService<>(Slot.class, jwtDecoder, Slot::getSlotId);
    }

}
