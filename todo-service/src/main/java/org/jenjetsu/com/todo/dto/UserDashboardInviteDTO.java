package org.jenjetsu.com.todo.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDashboardInviteDTO(
    @JsonProperty(value = "dashboard_id", required = true) UUID dashboardId, 
    @JsonProperty("user_id") UUID userId, 
    String username, 
    String email) {
    
}
