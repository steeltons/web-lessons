package org.jenjetsu.com.todo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import org.jenjetsu.com.todo.model.TaskActivity;
import org.jenjetsu.com.todo.serializer.DTOInstantSerializer;

import java.time.Instant;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskActivityReturnDTO(
        @JsonProperty("task_activity_id") UUID taskActivityId,
        String title,
        String description,
        @JsonProperty("task_status") String taskStatus,
        @JsonProperty("user_id") UUID userId,
        @JsonProperty("task_id") UUID taskId,
        boolean deleted,
        @JsonProperty("created_by") UUID createdBy,
        @JsonSerialize(using = DTOInstantSerializer.class)
        @JsonProperty("created_at") Instant createdAt) {

    public static TaskActivityReturnDTO from(TaskActivity activity) {
        return TaskActivityReturnDTO.builder()
                .taskActivityId(activity.getTaskActivityId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .taskStatus(activity.getActivityStatus().getStatus())
                .deleted(activity.getDeleted())
                .userId(activity.getUser() != null ? activity.getUser().getUserId() : null)
                .taskId(activity.getTask() != null ? activity.getTask().getTaskId() : null)
                .createdBy(activity.getCreatedBy() != null ? activity.getCreatedBy().getUserId() : null)
                .createdAt(activity.getCreatedAt().toInstant())
                .build();

    }
}
