package org.jenjetsu.com.finalproject.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.finalproject.dto.TaskCreateDTO;
import org.jenjetsu.com.finalproject.dto.TaskReturnDTO;
import org.jenjetsu.com.finalproject.model.Task;
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

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{Id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskReturnDTO getTaskById(@PathVariable("Id") UUID id) {
        Task model = this.taskService.readById(id);
        return TaskReturnDTO.convert(model);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MANAGER')")
    public Map<String, List<TaskReturnDTO>> getAllTasks() {
        List<TaskReturnDTO> dtos = this.taskService.readAll()
                                          .stream()
                                          .map(TaskReturnDTO::convert)
                                          .toList();
        return Map.of("task_list", dtos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> createTask(@RequestBody TaskCreateDTO dto) {
        Task raw = TaskCreateDTO.convert(dto);
        raw = this.taskService.create(raw);
        return Map.of("task_id", raw.getTaskId().toString());
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable("projectId") UUID id) {
        this.taskService.deleteById(id);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putchTask(@RequestBody TaskReturnDTO dto) {
        Task mergingProject = TaskReturnDTO.convertToTask(dto);
        Task mergableProject = this.taskService.readById(dto.taskId());
        Task newProject = mergableProject.patchModel(mergingProject);
        this.taskService.update(newProject);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putTask(@RequestBody TaskReturnDTO dto) {
        Task model = TaskReturnDTO.convertToTask(dto);
        this.taskService.update(model);
    }
}
