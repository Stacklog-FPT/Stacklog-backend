package com.stacklog.document_service.config;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.core_service.utils.jwt.JwtDecoder;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.document_service.model.entities.Document;

@Configuration
public class RedisDocumentConfig {
    
    @Bean
    public Function<Document, String> classIdExtractor() {
        return Document::getDocumentId;
    }

    @Bean
    public RedisService<Document> redisDocumentService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            JwtDecoder jwtDecoder) {
        return new RedisService<>(Document.class, jwtDecoder);
    }

}
