package org.jenjetsu.com.todo.controller;

import static java.lang.String.format;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.todo.dto.DashboardCreateDTO;
import org.jenjetsu.com.todo.dto.DashboardReturnDTO;
import org.jenjetsu.com.todo.dto.UserDashboardInviteDTO;
import org.jenjetsu.com.todo.model.Dashboard;
import org.jenjetsu.com.todo.model.DashboardUserInvite;
import org.jenjetsu.com.todo.model.User;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;
import org.jenjetsu.com.todo.service.DashboardService;
import org.jenjetsu.com.todo.service.DashboardUserInviteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dashboards")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    private final DashboardUserInviteService dashboardInviteService;

    @GetMapping("/{dashboardId}")
    @ResponseStatus(HttpStatus.OK)
    public DashboardReturnDTO getDashboard(@PathVariable UUID dashboardId) {
        return DashboardReturnDTO.from(dashboardService.readById(dashboardId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, UUID> createDashboard(@RequestBody DashboardCreateDTO dto,
                                             JwtUserIdAuthenticationToken token) {
        User creator = User.builder().userId(token.getUserId()).build();
        Dashboard raw = Dashboard.builder()
            .name(dto.name())
            .isHidden(dto.isHidden())
            .createdBy(creator)
            .createdAt(Timestamp.from(Instant.now()))
            .isDeleted(false)
            .userList(Arrays.asList(creator))
            .build();
        raw = this.dashboardService.create(raw);
        return Map.of("dashboard_id", raw.getDashboardId());
    }

    @PatchMapping("/restore/{dashboardId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, ?> restoreDashboard(@PathVariable("dahboardId") UUID dashboardId) {
        this.dashboardService.changeDashboardDeleteStatus(dashboardId, false);
        return Map.of("message", format("Dashboard %s was restored", dashboardId));
    }

    @DeleteMapping("/{dashboardId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, ?> deleteDashboard(@PathVariable("dashboardId") UUID dashboardId) {
        this.dashboardService.changeDashboardDeleteStatus(dashboardId, true);
        return Map.of("message", format("Dashboard %s was moved to trash box", dashboardId));
    }


    /*
     * All about dashboard invite
     */

    @PostMapping("/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> inviteUser(@RequestBody UserDashboardInviteDTO dto,
                                          JwtUserIdAuthenticationToken token) {
        DashboardUserInvite rawInvite = DashboardUserInvite.builder()
            .dashboard(Dashboard.builder().dashboardId(dto.dashboardId()).build())
            .inviter(User.builder().userId(token.getUserId()).build())
            .receiver(User.builder()
                          .userId(dto.userId())
                          .username(dto.username())
                          .email(dto.email()).build())
            .expirationDate(Timestamp.from(Instant.now().plusSeconds(60 * 30)))
            .build();
        rawInvite = this.dashboardInviteService.create(rawInvite);
        return Map.of("message", 
                      String.format("User with id %s was invited to dashboard", 
                                    rawInvite.getReceiver().getUserId())
                     );
    }

    @PutMapping("/invite/{inviteId}")
    @ResponseStatus(HttpStatus.OK)
    public void acceptInvite(@PathVariable("inviteId") UUID inviteId) {
        this.dashboardInviteService.acceptInvite(inviteId);
    }

    @DeleteMapping("/invite/{inviteId}")
    @ResponseStatus(HttpStatus.OK)
    public void declineInvite(@PathVariable("inviteId") UUID inviteId) {
        this.dashboardInviteService.declineInvite(inviteId);
    }
}
