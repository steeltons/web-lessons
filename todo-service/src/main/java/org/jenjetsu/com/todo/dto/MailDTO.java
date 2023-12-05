package org.jenjetsu.com.todo.dto;

import java.util.Map;

import org.jenjetsu.com.todo.model.MailMessageType;

import lombok.Builder;

@Builder
public record MailDTO(
    String to, 
    MailMessageType messageType,
    Map<String, Object> messageParams) {
    
}
