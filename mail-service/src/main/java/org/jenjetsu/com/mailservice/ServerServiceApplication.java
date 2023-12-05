package org.jenjetsu.com.mailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.SneakyThrows;

@SpringBootApplication
public class ServerServiceApplication{

	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(ServerServiceApplication.class, args);
	}

}
