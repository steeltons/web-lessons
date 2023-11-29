package org.jenjetsu.com.todo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import org.jenjetsu.com.todo.model.TaskComment;
import org.jenjetsu.com.todo.serializer.DTOInstantSerializer;

import java.time.Instant;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskCommentaryReturnDTO(
        @JsonProperty("task_commentary_id") Long taskCommentaryId,
        String content,
        @JsonProperty("task_id") UUID taskId,
        @JsonProperty("uploaded_by") UUID uploadedBy,
        @JsonSerialize(using = DTOInstantSerializer.class)
        @JsonProperty("uploaded_at") Instant uploadedAt) {

    public static TaskCommentaryReturnDTO from(TaskComment comment) {
        return TaskCommentaryReturnDTO.builder()
                .taskCommentaryId(comment.getTaskCommentId())
                .content(comment.getContent())
                .taskId(comment.getTask() != null ? comment.getTask().getTaskId() : null)
                .uploadedBy(comment.getUploadedBy() != null ? comment.getUploadedBy().getUserId() : null)
                .uploadedAt(comment.getUploadedAt().toInstant())
                .build();
    }
}
