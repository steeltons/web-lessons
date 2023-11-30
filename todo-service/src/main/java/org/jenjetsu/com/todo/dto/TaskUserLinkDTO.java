package org.jenjetsu.com.todo.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskUserLinkDTO(
        @JsonProperty(value = "task_id", required = true) UUID taskId,
        @JsonProperty(value = "dashboard_id", required = true) UUID dashboardId,
        @JsonProperty(value = "user_id") UUID userId,
        String username,
        String email) {
}
