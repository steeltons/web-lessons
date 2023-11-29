package org.jenjetsu.com.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record TaskActivityCreateDTO(
        @JsonProperty(required = true) String title,
        @JsonProperty(required = false) String description,
        String username,
        String email,
        @JsonProperty(value = "user_id") UUID userId,
        @JsonProperty(value = "task_id", required = true) UUID taskId) {
}
