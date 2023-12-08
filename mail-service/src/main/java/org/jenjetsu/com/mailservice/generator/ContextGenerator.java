package org.jenjetsu.com.mailservice.generator;

import org.jenjetsu.com.mailservice.model.EmailContext;
import static org.jenjetsu.com.mailservice.model.EmailContext.builder;
import org.jenjetsu.com.mailservice.model.MailDTO;

public abstract class  ContextGenerator {
    
    protected final String FROM_EMAIL = "${spring.mail.username}";
    protected final String TEMPLATE_NAME;
    protected final String SUBJECT;

    public ContextGenerator(String subject, String templateName) {
        this.TEMPLATE_NAME = templateName;
        this.SUBJECT = subject;
    }

    public abstract EmailContext generateContext(MailDTO dto);

    protected final EmailContext.EmailContextBuilder prepare(MailDTO dto) {
        return builder().to(dto.to())
                        .from(FROM_EMAIL)
                        .subject(SUBJECT)
                        .context(dto.messageParams())
                        .template(TEMPLATE_NAME);
    }
}
