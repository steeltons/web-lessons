package org.jenjetsu.com.todo.model;

import java.util.stream.Stream;

public enum MailMessageType {
    
    DASHBOARD_INVITE, TASK_INVITE, TASK_NOTIFICATION, ACTIVITY_NOTIFICATION;

    public MailMessageType from(String messageType) {
        return Stream.of(MailMessageType.values())
                     .filter((type) -> type.name().equalsIgnoreCase(messageType))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(String.format("No such type %s", messageType)));
    }
}
