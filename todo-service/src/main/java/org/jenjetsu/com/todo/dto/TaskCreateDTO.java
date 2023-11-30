package org.jenjetsu.com.todo.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskCreateDTO(
        @JsonProperty(value = "dashboard_id", required = true) UUID dashboardId,
        @JsonProperty(required = true) String title,
        @JsonProperty(required = false) String description) {
}
