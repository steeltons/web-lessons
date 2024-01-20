package org.jenjetsu.com.finalproject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import lombok.SneakyThrows;

@SpringBootApplication
@EnableCaching
public class FinalProjectApplication {

	static DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");
	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(FinalProjectApplication.class, args);
	}

}
