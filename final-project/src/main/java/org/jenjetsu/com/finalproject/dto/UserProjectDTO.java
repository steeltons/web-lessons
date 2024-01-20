package org.jenjetsu.com.finalproject.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(type = "object", requiredProperties = {"user_id", "project_id"})
public record UserProjectDTO(
    @JsonProperty(value = "project_id", required = true)
    @Schema(type = "string", format = "uuid", example = "791805ce-4986-4248-90b7-f0d230d7e66a")
    UUID userId, 
    @JsonProperty(value = "user_id", required = true)
    @Schema(type = "string", format = "uuid", example = "cc9140d4-22d6-4ac3-bd0a-931edce58697")
    UUID projectId) {
    
}
