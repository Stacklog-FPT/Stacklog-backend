package com.stacklog.chat_service.config;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.chat_service.model.entities.BoxChat;
import com.stacklog.chat_service.model.entities.BoxChatUser;
import com.stacklog.chat_service.model.entities.ChatMessage;
import com.stacklog.core_service.utils.jwt.JwtDecoder;
import com.stacklog.core_service.utils.redis.RedisService;

@Configuration
public class RedisChatServiceConfig {
    
    @Bean
    public RedisService<ChatMessage> redisChatMessageService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder) {
        return new RedisService<>(ChatMessage.class, jwtDecoder, ChatMessage::getChatMessageId);
    }

    @Bean
    public RedisService<BoxChat> redisBoxChatMessage(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder) {
        return new RedisService<>(BoxChat.class, jwtDecoder, BoxChat::getBoxChatId);
    }

    @Bean
    public RedisService<BoxChatUser> redisBoxchatUserService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder) {
        return new RedisService<>(BoxChatUser.class, jwtDecoder, BoxChatUser::getBoxChatUserId);
    }

}
