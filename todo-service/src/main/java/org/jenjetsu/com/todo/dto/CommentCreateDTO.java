package org.jenjetsu.com.todo.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentCreateDTO(
    @JsonProperty(value = "content", required = true) String content, 
    @JsonProperty(value = "task_id", required = true) UUID taskId) {
    
}
