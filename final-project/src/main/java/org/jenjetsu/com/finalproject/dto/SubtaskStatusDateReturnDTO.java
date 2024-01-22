package org.jenjetsu.com.finalproject.dto;

import java.time.Instant;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.model.SubtaskStatus;
import org.jenjetsu.com.finalproject.model.SubtaskStatusDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SubtaskStatusDateReturnDTO(
    @Schema(type = "integer", example = "1")
    Long id, 
    @JsonProperty(value = "subtask_id")
    @Schema(type = "string", format = "uuid", example="d6a8c677-3702-49f2-9ec5-cd6ea845b4bc")
    UUID subtaskId, 
    @Schema(type = "object", ref="SubtaskStatusReturnDTO")
    SubtaskStatusReturnDTO status, 
    @JsonSerialize(using = DTOInstantSerializer.class)
    Instant date) {

    public static SubtaskStatusDateReturnDTO convert(SubtaskStatusDate date) {
        Subtask subtask = date.getSubtask();
        SubtaskStatus status = date.getSubtaskStatus();
        return SubtaskStatusDateReturnDTO.builder()
                                .subtaskId(subtask != null ? subtask.getSubtaskId() : null)
                                .status(SubtaskStatusReturnDTO.convert(status))
                                .date(Instant.ofEpochMilli(date.getDate().getTime()))
                                .build();
    } 
    
}
