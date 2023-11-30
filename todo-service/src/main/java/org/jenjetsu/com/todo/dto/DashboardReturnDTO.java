package org.jenjetsu.com.todo.dto;

import java.time.Instant;
import java.util.UUID;

import org.jenjetsu.com.todo.model.Dashboard;
import org.jenjetsu.com.todo.serializer.DTOInstantSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;

@Builder
public record DashboardReturnDTO(
    @JsonProperty("dashboard_id") UUID dashboardId, 
    String name, 
    @JsonProperty("creator_id") UUID creatorId,
    @JsonSerialize(using = DTOInstantSerializer.class)
    @JsonProperty("created_at") Instant createdAt,
    @JsonProperty("user_amount") Integer userAmount,
    @JsonProperty("task_amount") Integer taskAmount) {
    
    public static DashboardReturnDTO from(Dashboard dashboard) {
        return DashboardReturnDTO.builder()
                .dashboardId(dashboard.getDashboardId())
                .name(dashboard.getName())
                .creatorId(dashboard.getCreatedBy().getUserId())
                .createdAt(dashboard.getCreatedAt().toInstant())
                .userAmount(dashboard.getUserList().size())
                .taskAmount(dashboard.getTaskList().size())
                .build();
    }
}
