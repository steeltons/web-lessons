package org.jenjetsu.com.restapi.dto;

import java.util.UUID;

import org.jenjetsu.com.restapi.model.Task;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record TaskReturnDTO(
    @JsonProperty(value = "task_id")
    UUID taskId, 
    String title, 
    String status,
    String description,
    @JsonProperty(value = "user_id")
    UUID userId) {
    
    public static TaskReturnDTO from(Task task) {
        return TaskReturnDTO.builder()
            .taskId(task.getTaskId())
            .title(task.getTitle())
            .description(task.getDescription())
            .status(task.getStatus())
            .userId(task.getUser() != null ? task.getUser().getUserId() : null)
            .build();
    }
}
