package org.jenjetsu.com.restapi.dto;

import java.util.UUID;

import org.jenjetsu.com.restapi.model.User;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record UserReturnDTO(
    @JsonProperty(value = "user_id") 
    UUID userId,
    String username,
    String email,
    @JsonProperty(value = "tasks_amount")
    Integer tasksAmount) {
    
    public static UserReturnDTO from(User user) {
        return UserReturnDTO.builder()
            .userId(user.getUserId())
            .username(user.getUsername())
            .email(user.getEmail())
            .tasksAmount(user.getTaskList().size())
            .build();
    }
}
