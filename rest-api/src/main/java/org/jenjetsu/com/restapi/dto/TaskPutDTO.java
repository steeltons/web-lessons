package org.jenjetsu.com.restapi.dto;

import java.util.UUID;

import org.jenjetsu.com.restapi.model.Task;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskPutDTO(
    @JsonProperty(value = "task_id", required = true)
    UUID taskId, 
    @JsonProperty(required = true)
    String title, 
    String description, 
    String status) {
    
    public static Task from(TaskPutDTO putDTO) {
        return Task.builder()
            .taskId(putDTO.taskId)
            .title(putDTO.title)
            .description(putDTO.description != null ? putDTO.description : Task.STANDARD_DESCRIPTION)
            .status(putDTO.status != null ? putDTO.status : Task.STANDARD_STATUS)
            .build();
    }
}
