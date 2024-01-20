package org.jenjetsu.com.finalproject.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.finalproject.dto.SubtaskCreateDTO;
import org.jenjetsu.com.finalproject.dto.SubtaskReturnDTO;
import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.service.SubtaskService;
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
@RequestMapping("/api/v1/subtasks")
public class SubtaskController{
    
    private final SubtaskService subtaskService;

    public SubtaskController(SubtaskService subtaskService) {
        this.subtaskService = subtaskService;
    }

    @GetMapping("/{Id}")
    @ResponseStatus(HttpStatus.OK)
    public SubtaskReturnDTO getTaskById(@PathVariable("Id") UUID id) {
        Subtask model = this.subtaskService.readById(id);
        return SubtaskReturnDTO.convert(model);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MANAGER')")
    public Map<String, List<SubtaskReturnDTO>> getAllTasks() {
        List<SubtaskReturnDTO> dtos = this.subtaskService.readAll()
                                          .stream()
                                          .map(SubtaskReturnDTO::convert)
                                          .toList();
        return Map.of("subtask_list", dtos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> createTask(@RequestBody SubtaskCreateDTO dto) {
        Subtask raw = SubtaskCreateDTO.convert(dto);
        raw = this.subtaskService.create(raw);
        return Map.of("task_id", raw.getSubtaskId().toString());
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable("projectId") UUID id) {
        this.subtaskService.deleteById(id);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putchTask(@RequestBody SubtaskReturnDTO dto) {
        Subtask mergingProject = SubtaskReturnDTO.convertToSubtask(dto);
        Subtask mergableProject = this.subtaskService.readById(dto.taskId());
        Subtask newProject = mergableProject.patchModel(mergingProject);
        this.subtaskService.update(newProject);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putTask(@RequestBody SubtaskReturnDTO dto) {
        Subtask model = SubtaskReturnDTO.convertToSubtask(dto);
        this.subtaskService.update(model);
    }
}
