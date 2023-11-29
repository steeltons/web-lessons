package org.jenjetsu.com.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskCreateDTO(
        @JsonProperty(required = true) String title,
        @JsonProperty(required = false) String description) {
}
