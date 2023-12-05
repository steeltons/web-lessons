package org.jenjetsu.com.todo.mailgenerator;

import java.util.HashMap;
import java.util.Map;

import org.jenjetsu.com.todo.dto.MailDTO;
import org.jenjetsu.com.todo.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskNotificationMailGenerator implements MailDtoGenerator<Task> {

    public MailDTO generateMail(Task t) {
        Map<String, Object> params = new HashMap<>();
        params.put("notificationTitle", t.getTitle());

        return null;
    }
}
