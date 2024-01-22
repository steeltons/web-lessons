package org.jenjetsu.com.finalproject.dto;

import org.jenjetsu.com.finalproject.model.SubtaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(type = "object")
public record SubtaskStatusReturnDTO(
    @Schema(type = "integer", example = "1")
    Integer id, 
    @Schema(type = "name", example = "CREATED")
    String name) {
    
    public static SubtaskStatusReturnDTO convert(SubtaskStatus status) {
        return SubtaskStatusReturnDTO.builder()
                                     .name(status.getName())
                                     .id(status.getSubtaskStatusId())
                                     .build();
    }
}
