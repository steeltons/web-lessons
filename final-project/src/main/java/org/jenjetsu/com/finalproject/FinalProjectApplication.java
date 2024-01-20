package org.jenjetsu.com.finalproject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.jenjetsu.com.finalproject.repository.SubtaskRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;

import lombok.SneakyThrows;

@SpringBootApplication
@EnableCaching
public class FinalProjectApplication {

	static DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");
	@SneakyThrows
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(FinalProjectApplication.class, args);
		var repository = context.getBean(SubtaskRepository.class);
	}

}
