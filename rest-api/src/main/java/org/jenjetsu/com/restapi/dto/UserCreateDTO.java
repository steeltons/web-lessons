package org.jenjetsu.com.restapi.dto;

import java.util.Collections;
import java.util.List;

import org.jenjetsu.com.restapi.model.Task;
import org.jenjetsu.com.restapi.model.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserCreateDTO(
    @JsonProperty(required = true)
    String username, 
    @JsonProperty(required = true)
    String email,
    List<TaskCreateDTO> tasks) {
    
    public static User from(UserCreateDTO createDto) {
        return User.builder()
            .username(createDto.username)
            .email(createDto.email)
            .taskList(UserCreateDTO.convertTasks(createDto.tasks))
            .build();
    }

    private static List<Task> convertTasks(List<TaskCreateDTO> taskList) {
        if (taskList == null) return Collections.EMPTY_LIST;
        return taskList.stream()
                       .map(TaskCreateDTO::from)
                       .toList();
    }
}
