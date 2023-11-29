package org.jenjetsu.com.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ChangeStatusDTO(
        @JsonProperty("task_activity_id") UUID taskActivityId,
        @JsonProperty("task_id") UUID taskId,
        String status) {
}
