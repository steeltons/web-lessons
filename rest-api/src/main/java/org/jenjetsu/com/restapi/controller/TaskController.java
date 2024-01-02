package org.jenjetsu.com.restapi.controller;

import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.restapi.dto.TaskCreateDTO;
import org.jenjetsu.com.restapi.dto.TaskPutDTO;
import org.jenjetsu.com.restapi.dto.TaskReturnDTO;
import org.jenjetsu.com.restapi.model.Task;
import org.jenjetsu.com.restapi.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/todo")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;

    @GetMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskReturnDTO getTaskById(@PathVariable("taskId") UUID taskId) {
        Task task = taskService.readById(taskId);
        return TaskReturnDTO.from(task);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> createTask(@RequestBody TaskCreateDTO createDTO) {
        Task raw = TaskCreateDTO.from(createDTO);
        raw = taskService.create(raw);
        return Map.of("task_id", raw.getTaskId().toString());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchTask(@RequestBody TaskPutDTO updateDTO) {
        Task newTask = TaskPutDTO.from(updateDTO);
        Task oldTask = taskService.readById(updateDTO.taskId());
        oldTask.putTask(newTask);
        taskService.update(oldTask);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable("taskId") UUID taskId) {
        taskService.deleteById(taskId);
    }
}
