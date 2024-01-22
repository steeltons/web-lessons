package org.jenjetsu.com.finalproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import lombok.SneakyThrows;

@SpringBootApplication
@EnableCaching
public class FinalProjectApplication {
	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(FinalProjectApplication.class, args);
	}

}
