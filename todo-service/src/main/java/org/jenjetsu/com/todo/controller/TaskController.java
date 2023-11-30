package org.jenjetsu.com.todo.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.dto.TakeTaskDTO;
import org.jenjetsu.com.todo.dto.TaskCreateDTO;
import org.jenjetsu.com.todo.dto.TaskReturnDTO;
import org.jenjetsu.com.todo.dto.TaskUserLinkDTO;
import org.jenjetsu.com.todo.model.Dashboard;
import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.User;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;
import org.jenjetsu.com.todo.service.TaskService;
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
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskReturnDTO getTask(@PathVariable("taskId") UUID taskId) {
        return TaskReturnDTO.from(this.taskService.readById(taskId));
    }

    @GetMapping("/created-by-my")
    public Map<String, ?> getCreatedByMyTasks(JwtUserIdAuthenticationToken token) {
        List<TaskReturnDTO> dtoList = this.taskService.readAllByCreatorId(token.getUserId()).stream()
                .map(TaskReturnDTO::from)
                .toList();
        Map<String, ?> responseBody = Map.of("tasks", dtoList);
        return responseBody;
    }

    @GetMapping
    public Map<String, ?> getMyTasks(JwtUserIdAuthenticationToken token) {
        List<TaskReturnDTO> dtoList = this.taskService.readAllByUserId(token.getUserId()).stream()
                .map(TaskReturnDTO::from)
                .toList();
        Map<String, ?> responseBody = Map.of("tasks", dtoList);
        return responseBody;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, ?> saveNewTask(@RequestBody TaskCreateDTO dto,
                            JwtUserIdAuthenticationToken token) {
        Task rawTask = Task.builder()
                .title(dto.title())
                .description(dto.description())
                .dashboard(Dashboard.builder().dashboardId(dto.dashboardId()).build())
                .createdBy(User.builder().userId(token.getUserId()).build())
                .createdAt(Timestamp.from(Instant.now()))
                .deleted(false)
                .build();
        rawTask = this.taskService.create(rawTask);
        return Map.of("task_id", rawTask.getTaskId());
    }

    @PostMapping("/add-user")
    @ResponseStatus(HttpStatus.CREATED)
    public void linkUserWithTask(@RequestBody TaskUserLinkDTO userLinkDTO, 
                                 JwtUserIdAuthenticationToken token) {
        this.taskService.linkUserWithTask(userLinkDTO, token);
    }

    @PostMapping("/take")
    @ResponseStatus(HttpStatus.CREATED)
    public void takeTask(@RequestBody TakeTaskDTO dto,
                         JwtUserIdAuthenticationToken token) {
        this.taskService.addUserToTask(token.getUserId(), dto.taskId(), dto.dashboardId());
    }

    @PatchMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public void changeTaskStatus(@RequestBody ChangeStatusDTO statusDto) {
        this.taskService.changeTaskStatus(statusDto);
    }

    @PatchMapping("/restore/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public void restoreTask(@PathVariable("taskId") UUID taskId) {
        this.taskService.changeTaskDeleteStatus(taskId, false);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable("taskId") UUID taskId) {
        this.taskService.changeTaskDeleteStatus(taskId, true);
    }

}
