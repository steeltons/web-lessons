package org.jenjetsu.com.finalproject.dto;

import java.sql.Date;
import java.time.Instant;

import org.jenjetsu.com.finalproject.model.Project;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(type = "object", requiredProperties = {"end_date"})
public record ProjectCreateDTO(
    @JsonProperty(value = "project_name")
    @Schema(type = "string", example = "Super project")
    String projectName, 
    @JsonProperty(value = "end_date", required = true)
    @Schema(type = "string", format = "date", example = "15.12.2001")
    @JsonDeserialize(converter = DTOInstantDeserializer.class)
    Instant endDate) {
    
    public static Project convert(ProjectCreateDTO dto) {
        return Project.builder()
                      .name(dto.projectName())
                      .endDate(new Date(dto.endDate().toEpochMilli()))
                      .build();
    } 
}
