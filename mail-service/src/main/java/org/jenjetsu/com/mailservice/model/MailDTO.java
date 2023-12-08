package org.jenjetsu.com.mailservice.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record MailDTO(
    @JsonProperty(required = true) String to, 
    @JsonProperty(required = true) MailMessageType messageType, 
    @JsonProperty(defaultValue = "{}") Map<String, Object> messageParams) {
    
}
