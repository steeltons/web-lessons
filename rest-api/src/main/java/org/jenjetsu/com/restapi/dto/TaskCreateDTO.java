package org.jenjetsu.com.restapi.dto;

import java.util.UUID;

import org.jenjetsu.com.restapi.model.Task;
import org.jenjetsu.com.restapi.model.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskCreateDTO(
    @JsonProperty(required = true)
    String title, 
    String description,
    String status,
    @JsonProperty(value = "user_id", required = true)
    UUID userId) {
    
    public static Task from(TaskCreateDTO createDto) {
        return Task.builder()
            .title(createDto.title)
            .description(createDto.description != null ? createDto.description : Task.STANDARD_DESCRIPTION)
            .status(createDto.status != null ? createDto.status : Task.STANDARD_STATUS)
            .user(User.builder().userId(createDto.userId).build())
            .build();
    } 
}
