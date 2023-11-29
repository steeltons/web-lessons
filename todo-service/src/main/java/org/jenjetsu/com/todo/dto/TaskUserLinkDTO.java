package org.jenjetsu.com.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record TaskUserLinkDTO(
        @JsonProperty(value = "task_id", required = true) UUID taskId,
        @JsonProperty(value = "user_id") UUID userId,
        String username,
        String email) {
}
