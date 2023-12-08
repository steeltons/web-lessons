package org.jenjetsu.com.mailservice.broker;

import java.util.Map;

import org.jenjetsu.com.mailservice.generator.ContextGenerator;
import org.jenjetsu.com.mailservice.model.MailDTO;
import org.jenjetsu.com.mailservice.service.EmailSenderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailMessageListener {
    
    private final Map<String, ContextGenerator> contextGeneratorMap;
    private final EmailSenderService emailSenderService;

    @SneakyThrows
    @KafkaListener(
        topics = "${topic.mail-topic}", 
        groupId = "${spring.kafka.consumer.group-id}",
        properties = {"spring.json.value.default.type=org.jenjetsu.com.mailservice.MailDTO"})
    public void hadleSendMessageCommand(MailDTO mailDto) {
        log.info("Message consumed {}", mailDto);
        String mailTypeName = mailDto.messageType().name();
        ContextGenerator contextGenerator = this.contextGeneratorMap.get(mailTypeName);
        if(contextGenerator != null) {
            try {
                this.emailSenderService.sendEmail(contextGenerator.generateContext(mailDto));
            } catch (Exception e) {
                log.error("Error while sending message. Error message: {}", e.getMessage());
                throw e;
            }
        } else {
            log.error("Operation {} does not support!", mailTypeName);
        }
    }

}
