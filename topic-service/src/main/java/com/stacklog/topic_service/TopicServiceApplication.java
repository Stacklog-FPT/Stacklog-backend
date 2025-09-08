package com.stacklog.topic_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.stacklog.topic_service", "com.stacklog.core_service"})
@EnableJpaRepositories(basePackages = "com.stacklog.topic_service.model.repo")
@EntityScan(basePackages = "com.stacklog.topic_service.model.entities")
@EnableFeignClients
public class TopicServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TopicServiceApplication.class, args);
	}

}
