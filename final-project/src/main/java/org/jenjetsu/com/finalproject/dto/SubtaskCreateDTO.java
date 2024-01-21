package org.jenjetsu.com.finalproject.dto;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

import org.jenjetsu.com.finalproject.controller.DTOInstantDeserializer;
import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.model.Task;
import org.jenjetsu.com.finalproject.model.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(type = "object", requiredProperties = {""})
public record SubtaskCreateDTO(
    @Schema(type = "string", example = "Super task")
    String title, 
    @Schema(type = "string", example = "Super description")
    String description, 
    @JsonProperty(value = "start_date", required = true)
    @Schema(type = "string", format = "date", example = "15.12.2023")
    @JsonDeserialize(converter = DTOInstantDeserializer.class)
    Instant startDate, 
    @JsonProperty(value = "end_date", required = true)
    @Schema(type = "string", format = "date", example = "20.12.2023")
    @JsonDeserialize(converter = DTOInstantDeserializer.class)
    Instant endDate, 
    @JsonProperty(value = "user_id", required = true)
    @Schema(type = "string", format = "uuid", example = "d6a8c677-3702-49f2-9ec5-cd6ea845b4bc")
    UUID userId,
    @JsonProperty(value = "task_id", required = true)
    @Schema(type = "string", format = "uuid", example = "551a6162-d6cd-4aa2-8948-b39811a0b70d")
    UUID taskId) {
    
    public static Subtask convert(SubtaskCreateDTO dto) {
        return Subtask.builder()
                      .title(dto.title)
                      .description(dto.description)
                      .startDate(new Date(dto.startDate.toEpochMilli()))
                      .endDate(new Date(dto.endDate.toEpochMilli()))
                      .user(User.builder().userId(dto.userId).build())
                      .task(Task.builder().taskId(dto.taskId).build())
                      .build();
    }
}
