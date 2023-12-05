package org.jenjetsu.com.mailservice;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.SneakyThrows;

@SpringBootApplication
public class ServerServiceApplication{

	@SneakyThrows
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ServerServiceApplication.class, args);
		KafkaTemplate<String, Object> template = context.getBean(KafkaTemplate.class);
		Map<String, Object> params = new HashMap<>();
		params.put("receiverUsername", "Stey");
		params.put("creatorUsername", "Lakmilak");
		params.put("inviteLink", "http://localhost:8080/api/v1/dashboards/invite" + UUID.randomUUID());
		params.put("expirationTime", Duration.ofDays(2l).toMillis());
		MailDTO dtov2 = new MailDTO("tcvetkov.so@dvfu.ru", MailMessageType.DASHBOARD_INVITE, params);
		template.send("test-topic", dtov2);
	}

}
