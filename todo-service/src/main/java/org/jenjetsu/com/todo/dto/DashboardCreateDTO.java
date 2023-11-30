package org.jenjetsu.com.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DashboardCreateDTO(
    String name, 
    @JsonProperty("is_hidden") Boolean isHidden) {
    
}
