package org.jenjetsu.com.mailservice.service;

import org.jenjetsu.com.mailservice.model.EmailContext;

public interface EmailSenderService {
    
    public void sendEmail(EmailContext email);
}
