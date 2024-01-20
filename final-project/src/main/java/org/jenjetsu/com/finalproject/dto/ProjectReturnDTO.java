package org.jenjetsu.com.finalproject.dto;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Project;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(type = "object", requiredProperties = {"project_id", "end_date"})
public record ProjectReturnDTO(
    @JsonProperty(value = "project_id", required = true)
    @Schema(type = "string", format = "uuid", example = "41098fd4-ae79-44bf-bffc-31d167c3b0ac")
    UUID projectId, 
    @JsonProperty(value = "project_name")
    @Schema(type = "string", example = "Super project")
    String projectName, 
    @JsonProperty(value = "start_date")
    @Schema(type = "string", format = "date", example = "15.12.2023")
    Instant startDate, 
    @JsonProperty(value = "end_date", required = true)
    @Schema(type = "string", format = "date", example = "31.12.2023")
    Instant endDate, 
    @Schema(type = "boolean", example = "false")
    boolean deleted) {

    public static ProjectReturnDTO convert(Project project) {
        return ProjectReturnDTO.builder()
                               .projectId(project.getProjectId())
                               .projectName(project.getName())
                               .startDate(project.getStartDate().toInstant())
                               .endDate(project.getEndDate().toInstant())
                               .deleted(project.getDeleted())
                               .build();
    }

    public static Project convertToProject(ProjectReturnDTO dto) {
        return Project.builder()
                      .projectId(dto.projectId)
                      .name(dto.projectName)
                      .startDate(new Date(dto.startDate.toEpochMilli()))
                      .endDate(new Date(dto.endDate.toEpochMilli()))
                      .deleted(dto.deleted)
                      .build();
    }
    
}
