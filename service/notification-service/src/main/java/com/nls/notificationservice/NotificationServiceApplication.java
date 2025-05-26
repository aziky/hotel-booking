package com.nls.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@ConfigurationPropertiesScan("com.nls.notificationservice.infrastructure.properties")
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
