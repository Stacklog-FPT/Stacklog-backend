package com.stacklog.chat_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.stacklog.chat_service", "com.stacklog.core_service"})
@EnableJpaRepositories(basePackages = "com.stacklog.chat_service.model.repo")
@EntityScan(basePackages = "com.stacklog.chat_service.model.entities")
public class ChatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatServiceApplication.class, args);
	}

}
