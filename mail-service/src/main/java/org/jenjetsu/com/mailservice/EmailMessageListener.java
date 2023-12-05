package org.jenjetsu.com.mailservice;

import java.util.Map;
import java.util.function.Function;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailMessageListener {
    
    private final Map<String, Function<MailDTO, EmailContext>> contextGeneratorMap;
    private final EmailSenderService emailSenderService;

    @SneakyThrows
    @KafkaListener(
        topics = "${topic.mail-topic}", 
        groupId = "${spring.kafka.consumer.group-id}",
        properties = {"spring.json.value.default.type=org.jenjetsu.com.mailservice.MailDTO"})
    public void hadleSendMessageCommand(MailDTO mailDto) {
        log.info("Message consumed {}", mailDto);
        String mailTypeName = mailDto.messageType().name();
        Function<MailDTO, EmailContext> contextGenerator = this.contextGeneratorMap.get(mailTypeName);
        if(contextGenerator == null) {
            throw new UnsupportedOperationException(mailTypeName + " not supported");
        }
        try {
            this.emailSenderService.sendEmail(contextGenerator.apply(mailDto));
        } catch (Exception e) {
            log.error("Error while sending message. Error message: {}", e.getMessage());
            throw e;
        }
    }

}
