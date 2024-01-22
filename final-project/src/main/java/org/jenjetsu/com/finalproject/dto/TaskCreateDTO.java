package org.jenjetsu.com.finalproject.dto;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Project;
import org.jenjetsu.com.finalproject.model.Task;
import org.jenjetsu.com.finalproject.model.TaskDependency;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(type = "object", requiredProperties = {"project_id", "title", "start_date", "end_date"})
public record TaskCreateDTO(
        @JsonProperty(required = true) 
        @Schema(type = "string", example = "Super title")
        String title,
        @JsonProperty(required = false)
        @Schema(type = "string", example = "Some description") 
        String description,
        @JsonProperty(value = "start_date", required = true)
        @Schema(type = "string", format = "date", example = "15.12.2023")
        @JsonDeserialize(converter = DTOInstantDeserializer.class)
        Instant startDate,
        @JsonProperty(value = "end_date", required = true)
        @Schema(type = "string", format = "date", example = "16.12.2023")
        @JsonDeserialize(converter = DTOInstantDeserializer.class)
        Instant endDate,
        @JsonProperty(value = "project_id", required = true) 
        @Schema(type = "string", format = "uuid", example = "914a9b76-648e-4b56-81c1-4ed8ff1e0bf3")
        UUID projectId,
        @Schema(type = "array", ref="UUID")
        List<UUID> dependencies) {

        public static Task convert(TaskCreateDTO dto) {
                return Task.builder()
                           .title(dto.title)
                           .description(dto.description)
                           .startDate(new Date(dto.startDate.toEpochMilli()))
                           .endDate(new Date(dto.endDate.toEpochMilli()))
                           .project(Project.builder().projectId(dto.projectId).build())
                           .taskDependencyList(dto.dependencies
                                                  .stream()
                                                  .map((id) -> Task.builder().taskId(id).build())
                                                  .map((dep) -> new TaskDependency(null, null, dep))
                                                  .toList())   
                           .build();
        }
}
