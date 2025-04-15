package com.stacklog.task_service;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TaskServiceApplication {

	@Bean
	NewTopic notification() {
		return new NewTopic("notification", 2, (short) 1);
	}

	@Bean
	NewTopic statistic() {
		return new NewTopic("statistics", 1, (short) 1);
	}

	public static void main(String[] args) {
		SpringApplication.run(TaskServiceApplication.class, args);
	}

}
