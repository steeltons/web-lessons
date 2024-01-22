package org.jenjetsu.com.finalproject.dto;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.model.SubtaskStatusDate;
import org.jenjetsu.com.finalproject.model.Task;
import org.jenjetsu.com.finalproject.model.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(type = "object", requiredProperties = {"subtask_id", "title", "user_id", "task_id"})
public record SubtaskReturnDTO(
    @JsonProperty(value = "subtask_id", required = true)
    @Schema(type = "string", format = "uuid", example = "c09c95a6-a6f9-4414-b244-35357650fe58")
    UUID subtaskId, 
    @Schema(type = "string", example = "Some name")
    String title,
    @Schema(type = "string", example = "Some description") 
    String description, 
    @JsonProperty(value = "task_id", required = true)
    @Schema(type = "string", format = "uuid", example = "7beeac30-b9c7-4a1a-89d0-a48a9eb10e62")
    UUID taskId, 
    @JsonProperty(value = "start_date", required = true)
    @Schema(type = "string", format = "date", example = "15.12.2023")
    @JsonSerialize(using = DTOInstantSerializer.class)
    Instant startDate, 
    @JsonProperty(value = "end_date", required = true)
    @Schema(type = "string", format = "date", example = "20.12.2023")
    @JsonSerialize(using = DTOInstantSerializer.class)
    Instant endDate,
    @JsonProperty(value = "user_id", required = true)
    @Schema(type = "string", format = "uuid", example = "d6a8c677-3702-49f2-9ec5-cd6ea845b4bc")
    UUID userId,
    @Schema(type = "array", ref="SubtaskStatusDateDTO")
    List<SubtaskStatusDateReturnDTO> statuses,
    @JsonProperty(value = "creator_id", required = true)
    @Schema(type = "string", format = "uuid", example = "761c3d6e-ea75-44e6-a7c2-e8803f9fb182")
    UUID creatorId) {

    public static SubtaskReturnDTO convert(Subtask subtask) {
        return SubtaskReturnDTO.builder()
                               .subtaskId(subtask.getSubtaskId())
                               .title(subtask.getTitle())
                               .description(subtask.getDescription())
                               .taskId(subtask.getTask() != null ? subtask.getTask().getTaskId() : null)
                               .startDate(Instant.ofEpochMilli(subtask.getStartDate().getTime()))
                               .endDate(Instant.ofEpochMilli(subtask.getEndDate().getTime()))
                               .userId(subtask.getUser() != null ? subtask.getUser().getUserId() : null)
                               .statuses(convertStatuses(subtask.getSubtaskStatusList()))
                               .creatorId(subtask.getCreator() != null ? subtask.getCreator().getUserId() : null)
                               .build();
    }

    private static List<SubtaskStatusDateReturnDTO> convertStatuses(List<SubtaskStatusDate> list) {
        return list.stream()
                   .map(SubtaskStatusDateReturnDTO::convert)
                   .toList();
    }

    public static Subtask convertToSubtask(SubtaskReturnDTO dto) {
        Date start = dto.startDate != null ? new Date(dto.startDate.toEpochMilli()) : null;
        Date end = dto.endDate != null ? new Date(dto.endDate.toEpochMilli()) : null;
        return Subtask.builder()
                      .subtaskId(dto.subtaskId)
                      .title(dto.title)
                      .description(dto.description)
                      .startDate(start)
                      .endDate(end)
                      .task(Task.builder().taskId(dto.taskId).build())
                      .user(User.builder().userId(dto.userId).build())
                      .creator(User.builder().userId(dto.userId).build())
                      .build();
    }
    
}
