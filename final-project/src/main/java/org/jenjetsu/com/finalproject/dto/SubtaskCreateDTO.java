package org.jenjetsu.com.finalproject.dto;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.model.User;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    Instant startDate, 
    @JsonProperty(value = "task_id", required = true)
    @Schema(type = "string", format = "date", example = "20.12.2023")
    Instant endDate, 
    @JsonProperty(value = "user_id", required = true)
    @Schema(type = "string", format = "uuid", example = "d6a8c677-3702-49f2-9ec5-cd6ea845b4bc")
    UUID userId) {
    
    public static Subtask convert(SubtaskCreateDTO dto) {
        return Subtask.builder()
                      .title(dto.title)
                      .description(dto.description)
                      .startDate(new Date(dto.startDate.toEpochMilli()))
                      .endDate(new Date(dto.endDate.toEpochMilli()))
                      .user(User.builder().userId(dto.userId).build())
                      .build();
    }
}
