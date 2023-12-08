package org.jenjetsu.com.todo.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskActivityCreateDTO(
        @JsonProperty(required = true) String title,
        @JsonProperty(required = false) String description,
        @JsonProperty UUID userId,
        @JsonProperty(value = "task_id", required = true) UUID taskId) {
}
