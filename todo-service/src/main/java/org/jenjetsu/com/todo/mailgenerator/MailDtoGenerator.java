package org.jenjetsu.com.todo.mailgenerator;

import org.jenjetsu.com.todo.dto.MailDTO;

public interface MailDtoGenerator<E> {
    
    public MailDTO generateMail(E from);
}
