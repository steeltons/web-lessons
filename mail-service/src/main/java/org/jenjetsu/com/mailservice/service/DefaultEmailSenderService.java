package org.jenjetsu.com.mailservice.service;

import java.nio.charset.StandardCharsets;

import org.jenjetsu.com.mailservice.model.EmailContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class DefaultEmailSenderService implements EmailSenderService{
    
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    
    @SneakyThrows
    public void sendEmail(EmailContext email) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
            message, 
            MimeMessageHelper.MULTIPART_MODE_MIXED, 
            StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(email.getContext());
        String emailContent = templateEngine.process(email.getTemplate(), context);
        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());
        helper.setFrom(email.getFrom());
        helper.setText(emailContent, true);
        emailSender.send(message);
    }
}
