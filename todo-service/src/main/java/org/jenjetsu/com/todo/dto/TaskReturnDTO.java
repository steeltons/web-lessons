package org.jenjetsu.com.todo.dto;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.TaskActivity;
import org.jenjetsu.com.todo.model.TaskComment;
import org.jenjetsu.com.todo.serializer.DTOInstantSerializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskReturnDTO(
        @JsonProperty("task_id") UUID taskId,
        String title,
        String description,
        String status,
        boolean deleted,
        @JsonProperty("created_by") UUID createdBy,
        @JsonSerialize(using = DTOInstantSerializer.class)
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("user_amount") Integer userAmount,
        // @JsonProperty("activity_amount") Integer activityAmount,
        List<TaskActivityReturnDTO> activities,
        List<TaskCommentaryReturnDTO> commentaries) {

    public static TaskReturnDTO from(Task task) {
        return TaskReturnDTO.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().getStatus())
                .deleted(task.getDeleted())
                .createdBy(task.getCreatedBy() != null ? task.getCreatedBy().getUserId() : null)
                .createdAt(task.getCreatedAt().toInstant())
                .userAmount(task.getUserList().size())
                // .activityAmount(task.getActivityList().size())
                .activities(converTaskActivities(task.getActivityList()))
                .commentaries(convertTaskCommentaries(task.getCommentList()))
                .build();
    }

    private static List<TaskActivityReturnDTO> converTaskActivities(List<TaskActivity> list) {
        if(list == null) return null;
        return list.stream()
                   .filter(Objects::nonNull)
                   .map(TaskActivityReturnDTO::from)
                   .toList();
    }

    private static List<TaskCommentaryReturnDTO> convertTaskCommentaries(List<TaskComment> list) {
        if(list == null) return null;
        return list.stream()
                .filter(Objects::nonNull)
                .map(TaskCommentaryReturnDTO::from)
                .toList();
    }
}
