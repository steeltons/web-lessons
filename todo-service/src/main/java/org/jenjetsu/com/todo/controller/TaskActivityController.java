package org.jenjetsu.com.todo.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.dto.TaskActivityCreateDTO;
import org.jenjetsu.com.todo.dto.TaskActivityReturnDTO;
import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.TaskActivity;
import org.jenjetsu.com.todo.model.User;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;
import org.jenjetsu.com.todo.service.TaskActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class TaskActivityController {

    private final TaskActivityService activityService;

    @GetMapping("/{activityId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskActivityReturnDTO getTaskActivity(@PathVariable("activityId") UUID activityId) {
        TaskActivity taskActivity = this.activityService.readById(activityId);
        return TaskActivityReturnDTO.from(taskActivity);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Map<String, ?> getUserActivities(JwtUserIdAuthenticationToken authToken) {
        List<TaskActivityReturnDTO> dtoList = this.activityService.readAllByUserId(authToken.getUserId())
                .stream()
                .map(TaskActivityReturnDTO::from)
                .toList();
        Map<String, ?> responseBody = Map.of("activities", dtoList);
        return responseBody;
    }

    @GetMapping("/created-by-my")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, ?> getTaskActivitiesByCreatorId(JwtUserIdAuthenticationToken token) {
        List<TaskActivityReturnDTO> dtoList = this.activityService.readAllByCreatorId(token.getUserId()).stream()
                .map(TaskActivityReturnDTO::from)
                .toList();
        Map<String, ?> responseBody = Map.of("activities", dtoList);
        return responseBody;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, ?> createTaskActivity(@RequestBody TaskActivityCreateDTO dto,
                                             JwtUserIdAuthenticationToken token) {
        TaskActivity activity = TaskActivity.builder()
                .title(dto.title())
                .description(dto.description())
                .user(dto.userId() != null ? User.builder().userId(dto.userId()).build() : null)
                .task(Task.builder().taskId(dto.taskId()).build())
                .createdBy(User.builder().userId(token.getUserId()).build())
                .createdAt(Timestamp.from(Instant.now()))
                .deleted(false)
                .build();
        activity = this.activityService.create(activity);
        Map<String, ?> responseBody = Map.of("task_activity_id", activity.getTaskActivityId());
        return responseBody;
    }

    @PatchMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public void changeActivityStatus(@RequestBody ChangeStatusDTO statusDto,
                                     JwtUserIdAuthenticationToken token) {
        this.activityService.changeActivityStatus(statusDto);
    }

    @DeleteMapping("/{activityId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, ?> deleteTaskActivity(@PathVariable("activityId") UUID activityId) {
        this.activityService.changeActivityDeleteStatus(activityId, true);
        return Map.of("message", String.format("Activity %s was moved to trash bin", activityId));
    }

    @PatchMapping("/restore/{activityId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, ?> restoreTaskActivity(@PathVariable("activityId") UUID activityId) {
        this.activityService.changeActivityDeleteStatus(activityId, false);
        return Map.of("message", String.format("Activity %s was restored", activityId));
    }

    @PatchMapping("/subscribe/{activityId}")
    @ResponseStatus(HttpStatus.OK)
    public void subscribeToActivity(@PathVariable("activityId") UUID activityId, 
                             JwtUserIdAuthenticationToken token) {
        this.activityService.changeActivityUser(activityId, token.getUserId());
    }

    @PatchMapping("/usubscribe/{activityId}")
    @ResponseStatus(HttpStatus.OK)
    public void unsubscribeFromActivity(@PathVariable("activityId") UUID activityId, 
                                        JwtUserIdAuthenticationToken token) {
        this.activityService.changeActivityUser(activityId, null);
    }
}
