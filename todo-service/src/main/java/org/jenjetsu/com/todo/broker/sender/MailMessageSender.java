package org.jenjetsu.com.todo.broker.sender;

import org.jenjetsu.com.todo.dto.MailDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MailMessageSender {
    
    private final String mailTopic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private MailMessageSender(@Value("${topic.mail-topic}") String mailTopic,
                              KafkaTemplate<String, Object> kafkaTemplate) {
        this.mailTopic = mailTopic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMailMessage(MailDTO mailDto) {
        log.info("Send email to mail service: {}", mailDto);
        kafkaTemplate.send(mailTopic, mailDto);
    }
}
