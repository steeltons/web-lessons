package org.jenjetsu.com.finalproject.dto;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Task;
import org.jenjetsu.com.finalproject.model.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(type = "object", requiredProperties = {"task_id", "start_date", "end_date"})
public record TaskReturnDTO(
        @JsonProperty(value = "task_id", required = true) 
        @Schema(type = "sting", format = "uuid", example = "0c50edcb-850e-41e6-8e12-e126fc591c51")
        UUID taskId,
        @Schema(type = "string", example = "Some name")
        String title,
        @Schema(type = "string", example = "Some description")
        String description,
        @Schema(type = "booelan", example = "false")
        boolean deleted,
        @Schema(type = "string", format = "date", example = "15.12.2023")
        Instant startDate,
        @Schema(type = "string", format = "date", example = "20.12.2023")
        Instant endDate,
        @JsonProperty("created_by") 
        @Schema(type = "string", format = "uuid", example = "914a9b76-648e-4b56-81c1-4ed8ff1e0bf3")
        UUID creatorId,
        @JsonProperty("project_id") 
        @Schema(type = "string", format = "uuid", example = "8afaf514-b757-4742-bad6-a0920ab17dc3")
        UUID projectId
        ) {

    public static TaskReturnDTO convert(Task task) {
        return TaskReturnDTO.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .deleted(task.isDeleted())
                .creatorId(task.getCreator() != null ? task.getCreator().getUserId() : null)
                .startDate(task.getStartDate().toInstant())
                .endDate(task.getEndDate().toInstant())
                .build();
    }

    public static Task convertToTask(TaskReturnDTO dto) {
        return Task.builder()
                   .taskId(dto.taskId)
                   .title(dto.title)
                   .description(dto.description)
                   .deleted(dto.deleted)
                   .creator(User.builder().userId(dto.creatorId).build())
                   .startDate(new Date(dto.startDate.toEpochMilli()))
                   .endDate(new Date(dto.endDate.toEpochMilli()))
                   .build();
    }
}
