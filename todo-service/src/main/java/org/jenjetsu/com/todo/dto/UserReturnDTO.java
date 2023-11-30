package org.jenjetsu.com.todo.dto;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.TaskActivity;
import org.jenjetsu.com.todo.model.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserReturnDTO(
        @JsonProperty("user_id") UUID userId,
        String username,
        String email,
        String firstname,
        String lastname,
        @JsonProperty("phone_number") Long phoneNumber,
        Boolean blocked,
        List<TaskReturnDTO> tasks,
        List<TaskActivityReturnDTO> activities) {

    public static UserReturnDTO from(User user) {
        return UserReturnDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .phoneNumber(user.getPhoneNumber())
                .blocked(user.getBlocked())
                .tasks(convertTasks(user.getTaskList()))
                .activities(convertTaskActivities(user.getActivityList()))
                .build();
    }

    private static List<TaskReturnDTO> convertTasks(List<Task> list) {
        if(list == null) return null;
        return list.stream()
                .filter(Objects::nonNull)
                .map((task) -> {
                    task.setUserList(null);
                    task.setActivityList(null);
                    task.setCommentList(null);
                    return TaskReturnDTO.from(task);
                })
                .toList();
    }

    private static List<TaskActivityReturnDTO> convertTaskActivities(List<TaskActivity> list) {
        if(list == null) return null;
        return list.stream()
                .filter(Objects::nonNull)
                .map(TaskActivityReturnDTO::from)
                .toList();
    }
}
