package org.jenjetsu.com.finalproject.deserializer;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.SneakyThrows;

@Component
public class SubtaskDeserializer implements Converter<ObjectNode, Subtask> {
    
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    
    @Override
    @SneakyThrows
    public Subtask convert(ObjectNode node) {
        UUID userId = node.has("user_id") ? UUID.fromString(node.get("user_id").asText()) : null;
        return Subtask.builder()
                      .subtaskId(node.has("subtask_id") ? UUID.fromString(node.get("subtask_id").asText()) : null)
                      .title(node.get("title").asText())
                      .description(node.has("description") ? node.get("description").asText() : null)
                      .user(User.builder().userId(userId).build())
                      .startDate(new Date(dateFormat.parse(node.get("start_date").asText()).getTime()))
                      .endDate(new Date(dateFormat.parse(node.get("end_date").asText()).getTime()))
                      .build();
    }
}
