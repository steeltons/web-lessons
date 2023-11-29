package org.jenjetsu.com.todo.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.dto.TaskCreateDTO;
import org.jenjetsu.com.todo.dto.TaskReturnDTO;
import org.jenjetsu.com.todo.dto.TaskUserLinkDTO;
import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.User;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;
import org.jenjetsu.com.todo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public UUID saveNewTask(@RequestBody TaskCreateDTO dto,
                            JwtUserIdAuthenticationToken token) {
        Task rawTask = Task.builder()
                .title(dto.title())
                .description(dto.description())
                .createdBy(User.builder().userId(token.getUserId()).build())
                .createdAt(Timestamp.from(Instant.now()))
                .deleted(false)
                .build();
        return this.taskService.create(rawTask).getTaskId();
    }

    @PostMapping("/add-user")
    @ResponseStatus(HttpStatus.CREATED)
    public void linkUserWithTask(@RequestBody TaskUserLinkDTO userLinkDTO, JwtUserIdAuthenticationToken token) {
        this.taskService.linkUserWithTask(userLinkDTO, token);
    }

    @PostMapping("/take/{taskId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void takeTask(@PathVariable("taskId") UUID taskId,
                         JwtUserIdAuthenticationToken token) {
        Task task = this.taskService.readByIdFetchUserList(taskId);
        task.getUserList().add(User.builder().userId(token.getUserId()).build());
        this.taskService.update(task);
    }

    @PatchMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public void changeTaskStatus(@RequestBody ChangeStatusDTO statusDTO,
                                 JwtUserIdAuthenticationToken token) {
        this.taskService.changeTaskStatus(statusDTO, token);
    }

    @PatchMapping("/restore/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public void restoreTask(@PathVariable("taskId") UUID taskId, JwtUserIdAuthenticationToken token) {
        this.taskService.changeTaskDeleteStatus(taskId, false, token);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable("taskId") UUID taskId, JwtUserIdAuthenticationToken token) {
        this.taskService.changeTaskDeleteStatus(taskId, true, token);
    }

}
