package org.jenjetsu.com.finalproject.serializer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.jenjetsu.com.finalproject.model.Subtask;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class SubtaskSerializer implements Converter<Subtask, ObjectNode> {
    
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    
    @Override
    public ObjectNode convert(Subtask subtask) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("subtaskId", subtask.getSubtaskId().toString());
        node.put("title", subtask.getTitle());
        node.put("description", subtask.getDescription());
        node.put("creator_id", subtask.getCreator() != null ? subtask.getCreator().getUserId().toString() : null);
        node.put("start_date", dateFormat.format(subtask.getStartDate()));
        node.put("end_date", dateFormat.format(subtask.getEndDate()));
        node.put("deleted", subtask.isDeleted());
        node.put("user_id", subtask.getUser().getUserId().toString());
        return node;
    }
}
