package com.moath.wordlebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WordleBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordleBotApplication.class, args);
	}

}
