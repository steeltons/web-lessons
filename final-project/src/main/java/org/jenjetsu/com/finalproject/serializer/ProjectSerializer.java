package org.jenjetsu.com.finalproject.serializer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.jenjetsu.com.finalproject.model.Project;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class ProjectSerializer implements Converter<Project, ObjectNode>{

    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public ObjectNode convert(Project project) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("project_id", project.getProjectId().toString());
        node.put("name", project.getName());
        node.put("deleted", project.getDeleted());
        node.put("start_date", dateFormat.format(project.getStartDate()));
        node.put("end_date", dateFormat.format(project.getEndDate()));
        node.put("creator_id", project.getCreatedBy() != null ? project.getCreatedBy().getUserId().toString() : null);
        return node;
    }
    
}
