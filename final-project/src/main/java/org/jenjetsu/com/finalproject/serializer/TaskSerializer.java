package org.jenjetsu.com.finalproject.serializer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.jenjetsu.com.finalproject.model.Task;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class TaskSerializer implements Converter<Task, ObjectNode> {
    
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public ObjectNode convert(Task task) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("task_id", task.getTaskId().toString());
        objectNode.put("title", task.getTitle());
        objectNode.put("description", task.getDescription());
        objectNode.put("deleted", task.isDeleted());
        objectNode.put("creator_id", task.getCreator() != null ? task.getCreator().getUserId().toString() : null);
        objectNode.put("start_date", dateFormat.format(task.getStartDate()));
        objectNode.put("end_date", dateFormat.format(task.getEndDate()));
        return objectNode;
    }
}
