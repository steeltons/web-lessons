package org.jenjetsu.com.finalproject.deserializer;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.finalproject.exception.EntityValidateException;
import org.jenjetsu.com.finalproject.model.Project;
import org.jenjetsu.com.finalproject.model.Task;
import org.jenjetsu.com.finalproject.model.TaskDependency;
import org.jenjetsu.com.finalproject.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.SneakyThrows;

@Component
public class TaskDeserializer implements Converter<ObjectNode, Task> {
    
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    @SneakyThrows
    public Task convert(ObjectNode node) {
        UUID projectId = UUID.fromString(node.get("project_id").asText());
        return Task.builder()
                   .taskId(node.has("task_id") ? UUID.fromString(node.get("task_id").asText()) : null)
                   .title(node.get("title").asText())
                   .description(node.has("description") ? node.get("description").asText() : null)
                   .startDate(new Date(dateFormat.parse(node.get("start_date").asText()).getTime()))
                   .endDate(new Date(dateFormat.parse(node.get("end_date").asText()).getTime()))
                   .project(Project.builder().projectId(projectId).build())
                   .creator(User.builder().userId(UUID.fromString("6a94b779-4d9a-4e8e-b6f3-8055fc5e2e42")).build())
                   .taskDependencyList(this.parseDependencies(node.arrayNode()))
                   .build();
    }

    private List<TaskDependency> parseDependencies(ArrayNode rawDependencies) {
        List<TaskDependency> dependencies = new ArrayList<>();
        for(var iter = rawDependencies.iterator(); iter.hasNext(); ) {
            UUID dependency = UUID.fromString(iter.next().asText());
            Task requiredTask = Task.builder().taskId(dependency).build();
            dependencies.add(new TaskDependency(null, null, requiredTask));
        } 
        return dependencies;
    }
}
