package org.jenjetsu.com.finalproject.dto;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Project;
import org.jenjetsu.com.finalproject.model.Task;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(type = "object", requiredProperties = {"project_id", "title", "start_date", "end_date"})
public record TaskCreateDTO(
        @JsonProperty(value = "project_id", required = true) 
        @Schema(type = "string", format = "uuid", example = "914a9b76-648e-4b56-81c1-4ed8ff1e0bf3")
        UUID projectId,
        @JsonProperty(required = true) 
        @Schema(type = "string", example = "Super title")
        String title,
        @JsonProperty(required = false)
        @Schema(type = "string", example = "Some description") 
        String description,
        @JsonProperty(value = "start_date", required = true)
        @Schema(type = "string", format = "date", example = "15.12.2023")
        Instant startDate,
        @JsonProperty(value = "end_date", required = true)
        @Schema(type = "string", format = "date", example = "16.12.2023")
        Instant endDate) {

        public static Task convert(TaskCreateDTO dto) {
                return Task.builder()
                           .title(dto.title)
                           .description(dto.description)
                           .startDate(new Date(dto.startDate.toEpochMilli()))
                           .endDate(new Date(dto.endDate.toEpochMilli()))
                           .project(Project.builder().projectId(dto.projectId).build())
                           .build();
        }
}
