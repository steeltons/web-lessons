package org.jenjetsu.com.finalproject.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.finalproject.dto.TaskCreateDTO;
import org.jenjetsu.com.finalproject.dto.TaskReturnDTO;
import org.jenjetsu.com.finalproject.model.Task;
import org.jenjetsu.com.finalproject.model.User;
import org.jenjetsu.com.finalproject.repository.TaskDependencyRepository;
import org.jenjetsu.com.finalproject.security.model.BearerTokenAuthentication;
import org.jenjetsu.com.finalproject.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    
    private final TaskService taskService;
    private final TaskDependencyRepository dependencyRep;

    public TaskController(TaskService taskService,
                          TaskDependencyRepository dependencyRep) {
        this.taskService = taskService;
        this.dependencyRep = dependencyRep;
    }

    @GetMapping("/{Id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public TaskReturnDTO getTaskById(@PathVariable("Id") UUID id) {
        Task model = this.taskService.readById(id);
        return TaskReturnDTO.convert(model);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public Map<String, List<TaskReturnDTO>> getAllTasks() {
        List<TaskReturnDTO> dtos = this.taskService.readAll()
                                          .stream()
                                          .map(TaskReturnDTO::convert)
                                          .toList();
        return Map.of("task_list", dtos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    public Map<String, String> createTask(@RequestBody TaskCreateDTO dto,
                                          BearerTokenAuthentication token) {
        Task raw = TaskCreateDTO.convert(dto);
        raw.setCreator(User.builder().userId(token.getUserId()).build());
        raw = this.taskService.create(raw);
        return Map.of("task_id", raw.getTaskId().toString());
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    public void deleteTask(@PathVariable("taskId") UUID id) {
        this.taskService.deleteById(id);
    }

    @PostMapping("/restore/{taskId}")
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    @ResponseStatus(HttpStatus.OK)
    public void restoreDeletedTask(@PathVariable("taskId") UUID id) {
        this.taskService.restoreTask(id);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    public void patchTask(@RequestBody TaskReturnDTO dto) {
        Task mergingTask = TaskReturnDTO.convertToTask(dto);
        this.taskService.update(mergingTask);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    public void putTask(@RequestBody TaskReturnDTO dto) {
        Task model = TaskReturnDTO.convertToTask(dto);
        this.taskService.update(model);
    }
}
