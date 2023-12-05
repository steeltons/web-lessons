package org.jenjetsu.com.mailservice;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Builder
public class EmailContext {
    
    private String from;
    private String to;
    private String subject;
    private String email;
    private String attachment;
    private String emailLanguage;
    private String fromDisplayName;
    private String displayName;
    private String template;
    private Map<String, Object> context;

}
