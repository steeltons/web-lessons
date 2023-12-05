package org.jenjetsu.com.todo.broker.sender;

import org.jenjetsu.com.todo.dto.MailDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailMessageSender {
    
    private final String mailTopic = "${topic.mail-topic}";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMailMessage(MailDTO mailDto) {
        log.info("Send email to mail service: {}", mailDto);
        kafkaTemplate.send(mailTopic, mailDto);
    }
}
