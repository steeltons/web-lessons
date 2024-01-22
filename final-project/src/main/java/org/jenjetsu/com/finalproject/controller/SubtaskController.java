package org.jenjetsu.com.finalproject.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.finalproject.dto.SubtaskCreateDTO;
import org.jenjetsu.com.finalproject.dto.SubtaskReturnDTO;
import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.model.User;
import org.jenjetsu.com.finalproject.security.model.BearerTokenAuthentication;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    public SubtaskReturnDTO getSubtaskById(@PathVariable("Id") UUID id) {
        Subtask model = this.subtaskService.readById(id);
        return SubtaskReturnDTO.convert(model);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Map<String, List<SubtaskReturnDTO>> getAllMySubtasks(
                                @RequestParam(name = "projectId", required = false) UUID projectId,
                                @RequestParam(name = "not_completed", defaultValue = "false") Boolean notCompleted,
                                BearerTokenAuthentication token) {
        List<SubtaskReturnDTO> dtos = this.subtaskService
                                          .readAllUserSubtasksByParams(token.getUserId(), projectId, notCompleted)
                                          .stream()
                                          .map(SubtaskReturnDTO::convert)
                                          .toList();
        return Map.of("subtask_list", dtos);

    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public Map<String, List<SubtaskReturnDTO>> getAllSubtasks() {
        List<SubtaskReturnDTO> dtos = this.subtaskService.readAll()
                                          .stream()
                                          .map(SubtaskReturnDTO::convert)
                                          .toList();
        return Map.of("subtask_list", dtos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    public Map<String, String> createSubtask(@RequestBody SubtaskCreateDTO dto,
                                          BearerTokenAuthentication token) {
        Subtask raw = SubtaskCreateDTO.convert(dto);
        raw.setCreator(User.builder().userId(token.getUserId()).build());
        raw = this.subtaskService.create(raw);
        return Map.of("subtask_id", raw.getSubtaskId().toString());
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    public void deleteSubtask(@PathVariable("projectId") UUID id) {
        this.subtaskService.deleteById(id);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    public void patchSubtask(@RequestBody SubtaskReturnDTO dto) {
        Subtask mergingProject = SubtaskReturnDTO.convertToSubtask(dto);
        Subtask mergableProject = this.subtaskService.readById(dto.taskId());
        mergableProject.merge(mergingProject);
        this.subtaskService.update(mergableProject);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    public void putSubtask(@RequestBody SubtaskReturnDTO dto) {
        Subtask model = SubtaskReturnDTO.convertToSubtask(dto);
        this.subtaskService.update(model);
    }
}
