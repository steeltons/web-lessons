package org.jenjetsu.com.finalproject.deserializer;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.hibernate.usertype.UserTypeLegacyBridge;
import org.jenjetsu.com.finalproject.model.Project;
import org.jenjetsu.com.finalproject.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.SneakyThrows;

@Component
public class ProjectDeserializer implements Converter<ObjectNode, Project> {
    
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    @SneakyThrows
    public Project convert(ObjectNode node) {
        return Project.builder()
                      .projectId(node.has("project_id") ? UUID.fromString(node.get("project_id").asText()) : null)
                      .name(node.has("name") ? node.get("name").asText() : null)
                      .endDate(new Date(dateFormat.parse(node.get("end_date").asText()).getTime()))
                      .createdBy(User.builder().userId(UUID.fromString("6a94b779-4d9a-4e8e-b6f3-8055fc5e2e42")).build())
                      .build();
    }
}
