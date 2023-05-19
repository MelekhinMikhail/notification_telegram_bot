package com.example.notification_telegram_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NotificationTelegramBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationTelegramBotApplication.class, args);
	}

}
