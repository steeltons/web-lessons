package org.jenjetsu.com.todo.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TakeTaskDTO(
    @JsonProperty(value = "task_id", required = true) UUID taskId, 
    @JsonProperty(value = "dashboard_id", required = true) UUID dashboardId) {
    
}
